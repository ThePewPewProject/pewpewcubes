package de.kleiner3.lasertag.mixin;

import com.google.gson.Gson;
import de.kleiner3.lasertag.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.item.LasertagVestItem;
import de.kleiner3.lasertag.item.LasertagWeaponItem;
import de.kleiner3.lasertag.lasertaggame.ILasertagGame;
import de.kleiner3.lasertag.lasertaggame.ITickable;
import de.kleiner3.lasertag.lasertaggame.PlayerDeactivatedManager;
import de.kleiner3.lasertag.lasertaggame.statistics.GameStats;
import de.kleiner3.lasertag.lasertaggame.statistics.StatsCalculator;
import de.kleiner3.lasertag.lasertaggame.timing.GameTickTimerTask;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.settings.SettingNames;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamConfigManager;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamDto;
import de.kleiner3.lasertag.util.Tuple;
import de.kleiner3.lasertag.util.serialize.TeamDtoSerializer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Interface injection into MinecraftServer to implement the lasertag game
 *
 * @author Étienne Muser
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ILasertagGame, ITickable {
    @Shadow
    public abstract ServerWorld getOverworld();

    private HashMap<TeamDto, ArrayList<BlockPos>> spawnpointCache = null;

    /**
     * Map every player to their team
     */
    private final HashMap<TeamDto, List<PlayerEntity>> teamMap = new HashMap<>();

    private final StatsCalculator statsCalculator = new StatsCalculator(teamMap);

    private List<LaserTargetBlockEntity> lasertargetsToReset = new LinkedList<>();

    private boolean isRunning = false;

    private ScheduledExecutorService gameTickTimer = null;

    /**
     * Inject into constructor of MinecraftServer
     *
     * @param ci The CallbackInfo
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        // Initialize team map
        for (TeamDto teamDto : TeamConfigManager.teamConfig.values()) {
            teamMap.put(teamDto, new LinkedList<>());
        }
    }

    //region ILasertagGame

    @Override
    public void startGame(boolean scanSpawnpoints) {
        // Reset all scores
        for (List<PlayerEntity> team : teamMap.values()) {
            for (PlayerEntity player : team) {
                player.resetLasertagScore();
            }
        }
        notifyPlayersAboutUpdate();

        // If spawnpoint cache needs to be filled
        if (spawnpointCache == null || scanSpawnpoints) {
            initSpawnpointCache();
        }

        // Get world
        World world = ((MinecraftServer) (Object) this).getOverworld();

        // Teleport players
        for (TeamDto teamDto : TeamConfigManager.teamConfig.values()) {
            List<PlayerEntity> team = teamMap.get(teamDto);

            for (PlayerEntity player : team) {
                // Get spawnpoints
                List<BlockPos> spawnpoints = spawnpointCache.get(teamDto);

                // If there are spawnpoints for this team
                if (spawnpoints.size() > 0) {
                    int idx = world.getRandom().nextInt(spawnpoints.size());

                    BlockPos destination = spawnpoints.get(idx);
                    player.requestTeleport(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);
                }
            }
        }

        // Start game
        isRunning = true;

        var preGameDelayTimer = Executors.newSingleThreadScheduledExecutor();
        var preGameDelay = (long)LasertagSettingsManager.get(SettingNames.START_TIME);
        preGameDelayTimer.schedule(() -> {

            // Activate every player
            for (List<PlayerEntity> team : teamMap.values()) {
                for (PlayerEntity player : team) {
                    PlayerDeactivatedManager.activate(player.getUuid(), world);
                    player.onActivated();
                }
            }

            // Start game tick timer
            gameTickTimer = Executors.newSingleThreadScheduledExecutor();
            gameTickTimer.scheduleAtFixedRate(new GameTickTimerTask(this), 0, 1, TimeUnit.MINUTES);

        }, preGameDelay, TimeUnit.SECONDS);

        // Notify players
        sendGameStartedEvent();
    }

    @Override
    public List<PlayerEntity> getPlayersOfTeam(TeamDto teamDto) {
        return teamMap.get(teamDto);
    }

    @Override
    public void playerJoinTeam(TeamDto newTeamDto, PlayerEntity player) {
        // Get new team
        var newTeam = teamMap.get(newTeamDto);

        // Check if team is full
        if (newTeam.size() >= (long)LasertagSettingsManager.get(SettingNames.MAX_TEAM_SIZE)) {
            // If is Server
            if (player instanceof ServerPlayerEntity) {
                ServerEventSending.sendErrorMessageToClient((ServerPlayerEntity) player, "Team " + newTeamDto.name() + " is full.");
            }
            return;
        }

        // Check if player is in a team already
        TeamDto oldTeamDto = null;
        for (TeamDto t : TeamConfigManager.teamConfig.values()) {
            if (teamMap.get(t).contains(player)) {
                oldTeamDto = t;
                break;
            }
        }

        // If player has no team
        if (oldTeamDto == null) {
            newTeam.add(player);
        } else {
            // If player tries to join his team again
            if (newTeamDto == oldTeamDto) {
                return;
            }

            // Swap team
            teamMap.get(oldTeamDto).remove(player);
            newTeam.add(player);
        }

        // Set team on player
        player.setTeam(newTeamDto);

        // Get players inventory
        var inventory = player.getInventory();

        // Clear players inventory
        inventory.clear();

        // Give player a lasertag vest
        var vestStack = new ItemStack(Items.LASERTAG_VEST);
        ((LasertagVestItem) Items.LASERTAG_VEST).setColor(vestStack, newTeamDto.color().getValue());
        player.equipStack(EquipmentSlot.CHEST, vestStack);

        // Give player a lasertag weapon
        var weaponStack = new ItemStack(Items.LASERTAG_WEAPON);
        ((LasertagWeaponItem) Items.LASERTAG_WEAPON).setColor(weaponStack, newTeamDto.color().getValue());
        ((LasertagWeaponItem) Items.LASERTAG_WEAPON).setDeactivated(weaponStack, true);
        inventory.setStack(0, weaponStack);

        // Notify about change
        notifyPlayersAboutUpdate();
    }

    @Override
    public void playerLeaveHisTeam(PlayerEntity player) {
        // For each team
        for (List<PlayerEntity> team : teamMap.values()) {
            // If the player is in the team
            if (team.contains(player)) {
                // Leave the team
                team.remove(player);
                player.setTeam(null);
                notifyPlayersAboutUpdate();
                return;
            }
        }
    }

    @Override
    public void onPlayerScored(PlayerEntity player, int score) {
        player.increaseScore(score);

        notifyPlayersAboutUpdate();
    }

    @Override
    public boolean isLasertagGameRunning() {
        return isRunning;
    }

    @Override
    public void syncTeamsAndScoresToPlayer(ServerPlayerEntity player) {
        var simplifiedTeamMap = buildSimplifiedTeamMap();

        // Serialize team map to json
        String messagesString = new Gson().toJson(simplifiedTeamMap);

        // Create packet buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Write team map string to buffer
        buf.writeString(messagesString);

        ServerPlayNetworking.send(player, NetworkingConstants.LASERTAG_GAME_TEAM_OR_SCORE_UPDATE, buf);
    }

    @Override
    public void registerLasertarget(LaserTargetBlockEntity target) {
        lasertargetsToReset.add(target);
    }

    //endregion

    //region ITickable

    /**
     * This method is called every minute when the game is running
     */
    @Override
    public void doTick() {
        System.out.println("Tick");
        // TODO
    }

    @Override
    public void endTick() {
        gameTickTimer.shutdown();
        gameTickTimer = null;
        lasertagGameOver();
    }

    //endregion

    //region Private methods

    /**
     * This method is called when the game ends
     */
    private void lasertagGameOver() {
        isRunning = false;

        sendGameOverEvent();

        // Get world
        World world = getOverworld();

        // Deactivate every player
        for (List<PlayerEntity> team : teamMap.values()) {
            for (PlayerEntity player : team) {
                PlayerDeactivatedManager.deactivate(player, world, true);
                player.onDeactivated();
            }
        }

        // Teleport players back to spawn
        for (var team : teamMap.values()) {
            for (var player : team) {
                player.requestTeleport(0.5F, 1, 0.5F);
            }
        }

        // Reset lasertargets
        for (var target : lasertargetsToReset) {
            target.reset();
        }
        lasertargetsToReset = new LinkedList<>();

        // Calculate stats
        statsCalculator.calcStats();

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Get last games stats
        var stats = statsCalculator.getLastGamesStats();

        // Get gson builder
        var gsonBuilder = TeamDtoSerializer.getSerializer();

        // To json
        var jsonString = gsonBuilder.create().toJson(stats, GameStats.class);

        // Write to buffer
        buf.writeString(jsonString);

        // Send statistics to clients
        ServerEventSending.sendToEveryone(getOverworld(), NetworkingConstants.GAME_STATISTICS, buf);

    }

    /**
     * Notifies every player of this world about a team or score update
     */
    private void notifyPlayersAboutUpdate() {
        var simplifiedTeamMap = buildSimplifiedTeamMap();

        // Serialize team map to json
        String messagesString = new Gson().toJson(simplifiedTeamMap);

        // Create packet buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Write team map string to buffer
        buf.writeString(messagesString);

        // Send to all clients
        ServerEventSending.sendToEveryone(((MinecraftServer) (Object) this).getOverworld(), NetworkingConstants.LASERTAG_GAME_TEAM_OR_SCORE_UPDATE, buf);
    }

    private HashMap<String, List<Tuple<String, Integer>>> buildSimplifiedTeamMap() {
        // Create simplified team map
        final HashMap<String, List<Tuple<String, Integer>>> simplifiedTeamMap = new HashMap<>();

        // For each team
        for (TeamDto t : TeamConfigManager.teamConfig.values()) {
            // Create a new list of (player name, player score) tuples
            List<Tuple<String, Integer>> playerDatas = new LinkedList<>();

            // For every player in the team
            for (PlayerEntity player : teamMap.get(t)) {
                // Add his name and score to the list
                playerDatas.add(new Tuple<>(player.getDisplayName().getString(), player.getLasertagScore()));
            }

            // Add the current team to the simplified team map
            simplifiedTeamMap.put(t.name(), playerDatas);
        }

        return simplifiedTeamMap;
    }

    /**
     * Notifies every player of this world about the start of a lasertag game
     */
    private void sendGameStartedEvent() {
        ServerWorld world = ((MinecraftServer) (Object) this).getOverworld();
        ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_STARTED, PacketByteBufs.empty());
    }

    private void sendGameOverEvent() {
        ServerWorld world = ((MinecraftServer) (Object) this).getOverworld();
        ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_OVER, PacketByteBufs.empty());
    }

    /**
     * Initializes the spawnpoint cache. Searches a 31 x 31 chunk area for spawnpoint blocks specified by the team.
     * This method is computationally intensive, don't call too often or when responsiveness is important. The call of this method blocks the server from ticking!
     */
    private void initSpawnpointCache() {

        // Initialize cache
        spawnpointCache = new HashMap<>();

        // Initialize team lists
        for (TeamDto team : TeamConfigManager.teamConfig.values()) {
            spawnpointCache.put(team, new ArrayList<>());
        }

        // Get the overworld
        ServerWorld world = ((MinecraftServer) (Object) this).getOverworld();

        // Start time measurement
        long startTime = System.nanoTime();

        // Iterate over blocks and find spawnpoints
        world.fastSearchBlock((block, pos) -> {
            for (TeamDto teamDto : TeamConfigManager.teamConfig.values()) {
                if (teamDto.spawnpointBlock().equals(block)) {
                    var team = spawnpointCache.get(teamDto);
                    synchronized (teamDto) {
                        team.add(pos);
                    }
                    break;
                }
            }
        }, (currChunk, maxChunk) -> {
            // Only send a progress update every second chunk to not ddos our players
            if (currChunk % 2 == 0) {
                return;
            }

            // Create packet buffer
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

            // Write progress to buffer
            buf.writeDouble((double) currChunk / (double) maxChunk);

            ServerEventSending.sendToEveryone(world, NetworkingConstants.PROGRESS, buf);
        });

        // Stop time measurement
        long stopTime = System.nanoTime();
        double duration = (stopTime - startTime) / 1000000000.0F;
        LasertagMod.LOGGER.info("Spawnpoint search took " + duration + "s.");
    }

    //endregion
}
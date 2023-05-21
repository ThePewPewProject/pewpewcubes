package de.kleiner3.lasertag.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.item.LasertagVestItem;
import de.kleiner3.lasertag.item.LasertagWeaponItem;
import de.kleiner3.lasertag.lasertaggame.ILasertagGame;
import de.kleiner3.lasertag.lasertaggame.ITickable;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamDtoSerializer;
import de.kleiner3.lasertag.lasertaggame.statistics.GameStats;
import de.kleiner3.lasertag.lasertaggame.statistics.StatsCalculator;
import de.kleiner3.lasertag.lasertaggame.timing.GameTickTimerTask;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
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
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Interface injection into MinecraftServer to implement the lasertag game
 *
 * @author Étienne Muser
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ILasertagGame, ITickable {
    //region Private fields

    private HashMap<TeamDto, ArrayList<BlockPos>> spawnpointCache = null;

    /**
     * Map every player to their team
     */
    private HashMap<TeamDto, List<UUID>> teamMap;

    private StatsCalculator statsCalculator;

    private List<LaserTargetBlockEntity> lasertargetsToReset = new LinkedList<>();

    private boolean isRunning = false;

    private ScheduledExecutorService gameTickTimer = null;

    //endregion

    @Shadow
    public abstract ServerWorld getOverworld();

    @Shadow
    public abstract boolean isDedicated();

    /**
     * Inject into constructor of MinecraftServer
     *
     * @param ci The CallbackInfo
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        // Init team map
        teamMap = new HashMap<>();

        // Initialize team map
        for (TeamDto teamDto : LasertagGameManager.getInstance().getTeamManager().teamConfig.values()) {
            teamMap.put(teamDto, new LinkedList<>());
        }

        // Init stats calculator
        statsCalculator = new StatsCalculator(((MinecraftServer) (Object) this));
    }

    /**
     * Inject into the stop method of the minecraft server.
     * This method gets called after entering the /stop command or typing stop into the server console.
     *
     * @param ci
     */
    @Inject(method = "shutdown", at = @At("HEAD"))
    private void atShutdown(CallbackInfo ci) {
        // Stop the lasertag game
        this.stopLasertagGame();

        // Dispose the game managers
        LasertagGameManager.getInstance().dispose();
    }

    //region ILasertagGame

    @Override
    public Optional<String> startGame(boolean scanSpawnpoints) {
        // Reset all scores
        LasertagGameManager.getInstance().getScoreManager().resetScores();
        notifyPlayersAboutUpdate();

        // If spawnpoint cache needs to be filled
        if (spawnpointCache == null || scanSpawnpoints) {
            initSpawnpointCache();
        }

        // Get world
        var world = getOverworld();

        // Check starting conditions
        var abortReasons = checkStartingConditions();

        // If should abort
        if (abortReasons.isPresent()) {
            // Send abort event to clients
            ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_START_ABORTED, new PacketByteBuf(Unpooled.buffer()));
            return abortReasons;
        }

        // Set gamerules
        ((MinecraftServer)(Object)this).getGameRules().get(GameRules.KEEP_INVENTORY).set(true, ((MinecraftServer)(Object)this));
        ((MinecraftServer)(Object)this).getGameRules().get(GameRules.DO_IMMEDIATE_RESPAWN).set(true, ((MinecraftServer)(Object)this));

        // Teleport players
        for (var teamDto : LasertagGameManager.getInstance().getTeamManager().teamConfig.values()) {
            var team = teamMap.get(teamDto);

            for (var playerUuid : team) {
                // Get spawnpoints
                var spawnpoints = spawnpointCache.get(teamDto);

                var player = ((MinecraftServer) (Object) this).getPlayerManager().getPlayer(playerUuid);

                // Sanity check
                if (player == null) {
                    continue;
                }

                int idx = world.getRandom().nextInt(spawnpoints.size());

                var destination = spawnpoints.get(idx);
                player.requestTeleport(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);

                // Set player to adventure gamemode
                player.changeGameMode(GameMode.ADVENTURE);

                // Get spawn pos
                var spawnPos = new BlockPos(destination.getX(), destination.getY() + 1, destination.getZ());

                // Set players spawnpoint
                player.setSpawnPoint(
                        World.OVERWORLD,
                        spawnPos, 0.0F, true, false);
            }
        }

        // Start game
        isRunning = true;

        var preGameDelayTimer = ThreadUtil.createScheduledExecutor("lasertag-server-pregame-delay-timer-thread-%d");
        var preGameDelay = LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.START_TIME);
        preGameDelayTimer.schedule(() -> {

            // Activate every player
            for (var team : teamMap.values()) {
                for (var playerUuid : team) {
                    LasertagGameManager.getInstance().getDeactivatedManager().activate(playerUuid, world);
                    ((MinecraftServer) (Object) this).getPlayerManager().getPlayer(playerUuid).onActivated();
                }
            }

            // Start game tick timer
            gameTickTimer = ThreadUtil.createScheduledExecutor("lasertag-server-game-tick-timer-thread-%d");
            gameTickTimer.scheduleAtFixedRate(new GameTickTimerTask(this), 0, 1, TimeUnit.MINUTES);

            // Stop the pre game delay timer
            ThreadUtil.attemptShutdown(preGameDelayTimer);

        }, preGameDelay, TimeUnit.SECONDS);

        // Notify players
        sendGameStartedEvent();

        // If is on dedicated server
        if (((MinecraftServer) (Object) this).isDedicated()) {
            // Set render data on server
            var renderData = LasertagGameManager.getInstance().getHudRenderManager();

            renderData.progress = 0.0;
            renderData.shouldRenderNameTags = false;

            // Start pregame count down timer
            renderData.startPreGameCountdownTimer(LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.START_TIME));
        }

        return Optional.empty();
    }

    @Override
    public boolean stopLasertagGame() {
        // If there is no game running
        if (!this.isRunning) {
            return false;
        }

        // Stop the game
        this.dispose();
        this.lasertagGameOver();

        return true;
    }

    @Override
    public boolean playerJoinTeam(TeamDto newTeamDto, PlayerEntity player) {
        // Get new team
        var newTeam = teamMap.get(newTeamDto);

        // Check if team is full
        if (newTeam.size() >= LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.MAX_TEAM_SIZE)) {
            // If is Server
            if (player instanceof ServerPlayerEntity) {
                ServerEventSending.sendErrorMessageToClient((ServerPlayerEntity) player, "Team " + newTeamDto.name() + " is full.");
            }
            return false;
        }

        // Check if player is in a team already
        TeamDto oldTeamDto = null;
        for (var t : LasertagGameManager.getInstance().getTeamManager().teamConfig.values()) {
            if (teamMap.get(t).contains(player.getUuid())) {
                oldTeamDto = t;
                break;
            }
        }

        // If player has no team
        if (oldTeamDto == null) {
            teamMap.get(newTeamDto).add(player.getUuid());
        } else {
            // If player tries to join his team again
            if (newTeamDto == oldTeamDto) {
                return true;
            }

            // Swap team
            teamMap.get(oldTeamDto).remove(player.getUuid());
            teamMap.get(newTeamDto).add(player.getUuid());
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

        // Sync to clients
        ServerEventSending.sendPlayerColorChanged(getOverworld(), player.getLasertagUsername(), newTeamDto.color().getValue());

        return true;
    }

    @Override
    public void playerLeaveHisTeam(PlayerEntity player) {
        // For each team
        for (var team : teamMap.values()) {
            // If the player is in the team
            if (team.contains(player.getUuid())) {
                // Leave the team
                team.remove(player.getUuid());
                player.setTeam(null);
                notifyPlayersAboutUpdate();
                ServerEventSending.sendPlayerColorChanged(getOverworld(), player.getLasertagUsername(), null);
                return;
            }
        }
    }

    @Override
    public void onPlayerScored(PlayerEntity player, long score) {
        LasertagGameManager.getInstance().getScoreManager().increaseScore(player.getUuid(), score);

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
        var messagesString = new Gson().toJson(simplifiedTeamMap);

        // Create packet buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write team map string to buffer
        buf.writeString(messagesString);

        ServerPlayNetworking.send(player, NetworkingConstants.LASERTAG_GAME_TEAM_OR_SCORE_UPDATE, buf);
    }

    @Override
    public void registerLasertarget(LaserTargetBlockEntity target) {
        lasertargetsToReset.add(target);
    }

    @Override
    public void notifyPlayersAboutUpdate() {
        var simplifiedTeamMap = buildSimplifiedTeamMap();

        // Serialize team map to json
        var messagesString = new Gson().toJson(simplifiedTeamMap);

        // Create packet buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write team map string to buffer
        buf.writeString(messagesString);

        // Send to all clients
        ServerEventSending.sendToEveryone(getOverworld(), NetworkingConstants.LASERTAG_GAME_TEAM_OR_SCORE_UPDATE, buf);

        // Update simplified team map in local copy of render data
        LasertagGameManager.getInstance().getHudRenderManager().teamMap = simplifiedTeamMap;
    }

    @Override
    public boolean isPlayerInTeam(ServerPlayerEntity player) {
        // For every team
        return teamMap
                .values()
                .stream()
                .anyMatch((team) -> team
                        .stream()
                        .anyMatch((playerUuid -> playerUuid
                                .equals(player.getUuid())
                        ))
                );
    }

    @Override
    public void dispose() {
        synchronized (this) {
            if (gameTickTimer == null) {
                return;
            }
            ThreadUtil.attemptShutdown(gameTickTimer);
            gameTickTimer = null;
        }
    }

    @Override
    public HashMap<String, List<Tuple<String, Long>>> getSimplifiedTeamMap() {
        return buildSimplifiedTeamMap();
    }

    @Override
    public TeamDto getTeamOfPlayer(UUID playerUuid) {
        return teamMap
                .entrySet()
                .stream()
                .filter((team) -> team.getValue().contains(playerUuid))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    //endregion

    //region ITickable

    /**
     * This method is called every minute when the game is running
     */
    @Override
    public void doTick() {
        // Here the music can be started
    }

    @Override
    public void endTick() {
        synchronized (this) {
            ThreadUtil.attemptShutdown(gameTickTimer);
            gameTickTimer = null;
        }

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
        var world = getOverworld();

        // Deactivate every player
        for (var team : teamMap.values()) {
            for (var playerUuid : team) {
                var player = ((MinecraftServer) (Object) this).getPlayerManager().getPlayer(playerUuid);

                // Sanity check
                if (player == null) {
                    continue;
                }

                LasertagGameManager.getInstance().getDeactivatedManager().deactivate(player, world, true);
                player.onDeactivated();
            }
        }

        // Teleport players back to spawn
        for (var team : teamMap.values()) {
            for (var playerUuid : team) {
                var player = ((MinecraftServer) (Object) this).getPlayerManager().getPlayer(playerUuid);

                // Sanity check
                if (player == null) {
                    continue;
                }

                player.requestTeleport(0.5F, 1, 0.5F);

                // Create block pos
                var origin = new BlockPos(0, 1, 0);

                // Set players spawnpoint
                player.setSpawnPoint(
                        World.OVERWORLD,
                        origin, 0.0F, true, false);

                // Set player to adventure gamemode
                player.changeGameMode(GameMode.ADVENTURE);
            }
        }

        // Reset lasertargets
        for (var target : lasertargetsToReset) {
            target.reset();
        }
        lasertargetsToReset = new LinkedList<>();

        try {
            // Calculate stats
            statsCalculator.calcStats();

            // Create packet
            var buf = new PacketByteBuf(Unpooled.buffer());

            // Get last games stats
            var stats = statsCalculator.getLastGamesStats();

            // Get gson builder
            var gsonBuilder = new GsonBuilder();

            // Get serializer
            var serializer = TeamDtoSerializer.getSerializer();

            // Register type adapter
            gsonBuilder.registerTypeAdapter(TeamDto.class, serializer);

            // To json
            var jsonString = gsonBuilder.create().toJson(stats, GameStats.class);

            // Write to buffer
            buf.writeString(jsonString);

            // Send statistics to clients
            ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_STATISTICS, buf);
        } catch (Exception e) {
            LasertagMod.LOGGER.error("ERROR:", e);
        }
    }

    private HashMap<String, List<Tuple<String, Long>>> buildSimplifiedTeamMap() {
        // Create simplified team map
        final HashMap<String, List<Tuple<String, Long>>> simplifiedTeamMap = new HashMap<>();

        // For each team
        for (var t : LasertagGameManager.getInstance().getTeamManager().teamConfig.values()) {
            // Create a new list of (player name, player score) tuples
            List<Tuple<String, Long>> playerDatas = new LinkedList<>();

            // For every player in the team
            for (var playerUuid : teamMap.get(t)) {
                // Add his name and score to the list
                playerDatas.add(new Tuple<>(((MinecraftServer) (Object) this).getPlayerManager().getConsistentPlayerUsername(playerUuid),
                        LasertagGameManager.getInstance().getScoreManager().getScore(playerUuid)));
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
        var world = getOverworld();
        ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_STARTED, PacketByteBufs.empty());
    }

    private void sendGameOverEvent() {
        var world = getOverworld();
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
        for (var team : LasertagGameManager.getInstance().getTeamManager().teamConfig.values()) {
            spawnpointCache.put(team, new ArrayList<>());
        }

        // Get the overworld
        var world = getOverworld();

        // Start time measurement
        var startTime = System.nanoTime();

        // Iterate over blocks and find spawnpoints
        world.fastSearchBlock((block, pos) -> {
            for (var teamDto : LasertagGameManager.getInstance().getTeamManager().teamConfig.values()) {
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
            var buf = new PacketByteBuf(Unpooled.buffer());

            // Write progress to buffer
            buf.writeDouble((double) currChunk / (double) maxChunk);

            ServerEventSending.sendToEveryone(world, NetworkingConstants.PROGRESS, buf);
        });

        // Stop time measurement
        var stopTime = System.nanoTime();
        var duration = (stopTime - startTime) / 1000000000.0;
        LasertagMod.LOGGER.info("Spawnpoint search took " + duration + "s.");
    }

    /**
     * Checks if all starting conditions are met. If the game can start, this method returns an empty optional
     * Otherwise it returns the reasons why the game can not start as a string.
     * @return
     */
    private Optional<String> checkStartingConditions() {
        boolean abort = false;
        var builder = new StringBuilder();

        // For every team
        for (var team : teamMap.entrySet()) {
            // If the team contains players
            if (team.getValue().size() > 0) {
                // Get the spawnpoints for the team
                var spawnpoints = spawnpointCache.get(team.getKey());

                // If the team has no spawnpoints
                if (spawnpoints.size() == 0) {
                    abort = true;
                    builder.append("  *No spawnpoints were found for team '" + team.getKey().name() + "'\n");
                }
            }
        }

        if (abort) {
            return Optional.of(builder.toString());
        }

        return Optional.empty();
    }

    //endregion
}
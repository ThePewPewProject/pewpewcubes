package de.kleiner3.lasertag.mixin;

import com.google.gson.Gson;
import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.GameStats;
import de.kleiner3.lasertag.lasertaggame.ILasertagGame;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.util.Tuple;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
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

/**
 * Interface injection into MinecraftServer to implement the lasertag game
 *
 * @author Étienne Muser
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ILasertagGame {
    @Shadow public abstract ServerWorld getOverworld();

    private GameStats lastGamesStats = null;

    private HashMap<Colors.Color, ArrayList<BlockPos>> spawnpointCache = null;

    /**
     * Map every player to their team color
     */
    private HashMap<Colors.Color, List<PlayerEntity>> teamMap = new HashMap<>();

    private boolean isRunning = false;

    /**
     * Inject into constructor of MinecraftServer
     *
     * @param ci
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        // Initialize team map
        for (Colors.Color color : Colors.colorConfig.values()) {
            teamMap.put(color, new LinkedList<>());
        }
    }

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

        // Teleport players
        for (Colors.Color teamColor : Colors.colorConfig.values()) {
            List<PlayerEntity> team = teamMap.get(teamColor);

            World world = ((MinecraftServer) (Object) this).getOverworld();

            for (PlayerEntity player : team) {
                // Get spawnpoints
                List<BlockPos> spawnpoints = spawnpointCache.get(teamColor);

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
        new Thread(() -> {
            // Wait for game start cooldown
            try {
                Thread.sleep(LasertagConfig.getInstance().getStartTime() * 1000);
            } catch (InterruptedException e) {}

            // Activate every player
            for (List<PlayerEntity> team : teamMap.values()) {
                for (PlayerEntity player : team) {
                    player.setDeactivated(false);
                }
            }

            for(int i = 0; i < LasertagConfig.getInstance().getPlayTime(); i++) {
                lasertagTick();
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                }
            }

            lasertagGameOver();
        }).start();

        // Notify players
        sendGameStartedEvent();
    }

    @Override
    public List<PlayerEntity> getPlayersOfTeam(Colors.Color color) {
        return teamMap.get(color);
    }

    @Override
    public void playerJoinTeam(Colors.Color newTeamColor, PlayerEntity player) {
        // Check if team is full
        if (teamMap.get(newTeamColor).size() >= LasertagConfig.getInstance().getMaxTeamSize()) {
            // If is Server
            if (player instanceof ServerPlayerEntity) {
                ServerEventSending.sendErrorMessageToClient((ServerPlayerEntity) player, "Team " + newTeamColor.getName() + " is full.");
            }
            return;
        }

        // Check if player is in a team already
        Colors.Color oldTeamColor = null;
        for (Colors.Color c : Colors.colorConfig.values()) {
            if (teamMap.get(c).contains(player)) {
                oldTeamColor = c;
                break;
            }
        }

        // If player has no team
        if (oldTeamColor == null) {
            teamMap.get(newTeamColor).add(player);
        } else {
            // If player tries to join his team again
            if (newTeamColor == oldTeamColor) return;

            // Swap team
            teamMap.get(oldTeamColor).remove(player);
            teamMap.get(newTeamColor).add(player);
        }

        // Notify about change
        notifyPlayersAboutUpdate();
    }

    @Override
    public void playerLeaveTeam(Colors.Color oldTeamColor, PlayerEntity player) {
        // Get the players in the team
        List<PlayerEntity> team = teamMap.get(oldTeamColor);

        // Check if player is in the team he claims to be
        if (!team.contains(player)) {
            return;
        }

        // Remove player from his team
        team.remove(player);

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
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * This method is called every minute when the game is running
     */
    private void lasertagTick() {
        // TODO
    }

    /**
     * This method is called when the game ends
     */
    private void lasertagGameOver() {
        isRunning = false;

        // Deactivate every player
        for (List<PlayerEntity> team : teamMap.values()) {
            for (PlayerEntity player : team) {
                player.setDeactivated(true);
            }
        }

        // Calculate stats
        calcStats();

        // Teleport players back to spawn
        var spawn = getOverworld().getSpawnPos();
        for (var team : teamMap.values()) {
            for (var player : team) {
                player.requestTeleport(spawn.getX() + 0.5F, spawn.getY(), spawn.getZ() + 0.5F);
            }
        }
    }

    /**
     * Notifies every player of this world about a team or score update
     */
    private void notifyPlayersAboutUpdate() {
        // Create simplified team map
        final HashMap<String, List<Tuple<String, Integer>>> simplifiedTeamMap = new HashMap<>();

        // For each color
        for (Colors.Color c : Colors.colorConfig.values()) {
            // Create a new list of (player name, player score) tuples
            List<Tuple<String, Integer>> playerDatas = new LinkedList<>();

            // For every player in the team
            for (PlayerEntity player : teamMap.get(c)) {
                // Add his name and score to the list
                playerDatas.add(new Tuple<>(player.getDisplayName().getString(), player.getLasertagScore()));
            }

            // Add the current team to the simplified team map
            simplifiedTeamMap.put(c.getName(), playerDatas);
        }

        // Serialize team map to json
        String messagesString = new Gson().toJson(simplifiedTeamMap);

        // Create packet buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Write team map string to buffer
        buf.writeString(messagesString);

        // Send to all clients
        ServerEventSending.sendToEveryone(((MinecraftServer) (Object) this).getOverworld(), NetworkingConstants.LASERTAG_GAME_TEAM_OR_SCORE_UPDATE, buf);
    }

    /**
     * Notifies every player of this world about the start of a lasertag game
     */
    private void sendGameStartedEvent() {
        ServerWorld world = ((MinecraftServer) (Object) this).getOverworld();
        ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_STARTED, PacketByteBufs.empty());
    }

    /**
     * Initializes the spawnpoint cache. Searches a 63 x 63 chunk area for spawnpoint blocks specified by the team color.
     * This method is computationally intensive, don't call too often or when responsiveness is important. The call of this method blocks the server from ticking!
     */
    public void initSpawnpointCache() {

        // Initialize cache
        spawnpointCache = new HashMap<>();

        // Initialize team lists
        for (Colors.Color team : Colors.colorConfig.values()) {
            spawnpointCache.put(team, new ArrayList<>());
        }

        // Get the overworld
        ServerWorld world = ((MinecraftServer) (Object) this).getOverworld();

        // Start time measurement
        long startTime = System.nanoTime();

        // Iterate over blocks and find spawnpoints
        world.fastSearchBlock((block, pos) -> {
            for (Colors.Color color : Colors.colorConfig.values()) {
                if (color.getSpawnpointBlock().equals(block)) {
                    var team = spawnpointCache.get(color);
                    synchronized (color) {
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
            buf.writeDouble((double)currChunk / (double)maxChunk);

            ServerEventSending.sendToEveryone(world, NetworkingConstants.PROGRESS, buf);
        });

        // Stop time measurement
        long stopTime = System.nanoTime();
        double duration = (stopTime - startTime) / 1000000000.0F;
        LasertagMod.LOGGER.info("Spawnpoint search took " + duration + "s.");
    }

    private void calcStats() {
        lastGamesStats = new GameStats();
        for (Colors.Color teamColor : teamMap.keySet()) {
            var team = teamMap.get(teamColor);
            int teamScore = 0;

            var playersOfThisTeam = new ArrayList<Tuple<PlayerEntity, Integer>>();
            for (PlayerEntity player : team) {
                int playerScore = player.getLasertagScore();
                var playerScoreTuple = new Tuple<>(player, playerScore);
                teamScore += playerScore;
                lastGamesStats.playerScores.add(playerScoreTuple);
                playersOfThisTeam.add(playerScoreTuple);
            }

            if (playersOfThisTeam.size() > 0) {
                lastGamesStats.teamPlayerScores.put(teamColor, playersOfThisTeam);
            }
            lastGamesStats.teamScores.add(new Tuple<>(teamColor, teamScore));
        }

        // Sort stats
        Collections.sort(lastGamesStats.teamScores, (a, b) -> (a.y - b.y));
        Collections.sort(lastGamesStats.playerScores, (a, b) -> (a.y - b.y));
        for (var team : lastGamesStats.teamPlayerScores.values()) {
            Collections.sort(team, (a, b) -> (a.y - b.y));
        }
    }
}

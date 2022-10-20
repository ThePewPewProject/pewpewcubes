package de.kleiner3.lasertag.mixin;

import com.google.gson.Gson;
import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.LasertagMod;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Interface injection into MinecraftServer to implement the lasertag game
 *
 * @author Étienne Muser
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ILasertagGame {

    private HashMap<Colors, ArrayList<BlockPos>> spawnpointCache = null;

    /**
     * Map every player to their team color
     */
    private HashMap<Colors, List<PlayerEntity>> teamMap = new HashMap<>();

    /**
     * Inject into constructor of MinecraftServer
     *
     * @param ci
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        // Initialize team map
        for (Colors color : Colors.values()) {
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
            initSpawnpointCache();;
        }

        // Teleport players
        for (Colors teamColor : Colors.values()) {
            List<PlayerEntity> team = teamMap.get(teamColor);

            World world = ((MinecraftServer) (Object) this).getOverworld();

            for (PlayerEntity player : team) {
                // Get spawnpoints
                List<BlockPos> spawnpoints = spawnpointCache.get(teamColor);

                // If there are spawnpoints for this team
                if (spawnpoints.size() > 0) {
                    BlockPos destination = spawnpoints.get(0);
                    player.requestTeleport(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);
                }
            }
        }

        // Start game

        // Notify players
        sendGameStartedEvent();
    }

    @Override
    public List<PlayerEntity> getPlayersOfTeam(Colors color) {
        return teamMap.get(color);
    }

    @Override
    public void playerJoinTeam(Colors newTeamColor, PlayerEntity player) {
        // Check if team is full
        if (teamMap.get(newTeamColor).size() >= LasertagConfig.maxTeamSize) {
            // If is Server
            if (player instanceof ServerPlayerEntity) {
                ServerEventSending.sendErrorMessageToClient((ServerPlayerEntity) player, "Team " + newTeamColor.name() + " is full.");
            }
            return;
        }

        // Check if player is in a team already
        Colors oldTeamColor = null;
        for (Colors c : Colors.values()) {
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
            teamMap.get(oldTeamColor).add(player);
        }

        // Notify about change
        notifyPlayersAboutUpdate();
    }

    @Override
    public void playerLeaveTeam(Colors oldTeamColor, PlayerEntity player) {
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
                return;
            }
        }

        notifyPlayersAboutUpdate();
    }

    @Override
    public void onPlayerScored(PlayerEntity player, int score) {
        player.increaseScore(score);

        notifyPlayersAboutUpdate();
    }

    private void notifyPlayersAboutUpdate() {
        // Create simplified team map
        final HashMap<Colors, List<Tuple<String, Integer>>> simplifiedTeamMap = new HashMap<>();

        // For each color
        for (Colors c : Colors.values()) {
            // Create a new list of (player name, player score) tuples
            List<Tuple<String, Integer>> playerDatas = new LinkedList<>();

            // For every player in the team
            for (PlayerEntity player : teamMap.get(c)) {
                // Add his name and score to the list
                playerDatas.add(new Tuple<String, Integer>(player.getDisplayName().getString(), player.getLasertagScore()));
            }

            // Add the current team to the simplified team map
            simplifiedTeamMap.put(c, playerDatas);
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

    private void sendGameStartedEvent() {
        ServerWorld world = ((MinecraftServer) (Object) this).getOverworld();
        ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_STARTED, PacketByteBufs.empty());
    }

    public void initSpawnpointCache() {

        // Initialize cache
        spawnpointCache = new HashMap<>();

        // Initialize team lists
        for (Colors team : Colors.values()) {
            spawnpointCache.put(team, new ArrayList<>());
        }

        // Get the overworld
        World world = ((MinecraftServer) (Object) this).getOverworld();

        // Start time measurement
        long startTime = System.nanoTime();

        // Iterate over blocks and find spawnpoints
        world.fastSearchBlock((block, pos) -> {
            for (Colors color : Colors.values()) {
                if (color.getSpawnpointBlock().equals(block)) {
                    var team = spawnpointCache.get(color);
                    synchronized (color) {
                        team.add(pos);
                    }
                    break;
                }
            }
        }, (currChunk, maxChunk) -> {
            LasertagMod.LOGGER.info("Searched chunk " + currChunk + "/" + maxChunk);
        });

        // Stop time measurement
        long stopTime = System.nanoTime();
        double duration = (stopTime - startTime) / 1000000000.0F;
        LasertagMod.LOGGER.info("Spawnpoint search took " + duration + "s.");
    }

    private static final int LOWER_SEARCH_BORDER = -1024;
    private static final int UPPER_SERACH_BORDER = 1024;
    private static final int WORLD_LOWER_BORDER = -64;
    private static final int WORLD_UPPER_BORDER = 319;
}

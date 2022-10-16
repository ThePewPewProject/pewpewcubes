package de.kleiner3.lasertag.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.Gson;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.lasertaggame.ILasertagGame;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.util.Tuple;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

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
    public void startGame() {
        // TODO Auto-generated method stub

        // Reset all scores
        for (List<PlayerEntity> team : teamMap.values()) {
            for (PlayerEntity player : team) {
                player.resetLasertagScore();
            }
        }
        notifyPlayersAboutUpdate();

        // If spawnpoint cache is not initialized
        if (spawnpointCache == null) {
            // Initialize cache
            spawnpointCache = new HashMap<>();

            // Initialize team lists
            for (Colors team : Colors.values()) {
                spawnpointCache.put(team, new ArrayList<>());
            }

            // Get the overworld
            World world = ((MinecraftServer)(Object)this).getOverworld();

            // Iterate over every block in the world
            for (int x = -1000; x < 1000; x++) {
                for (int y = -64; y < 200; y++) {
                    for (int z = -1000; z < 1000; z++) {
                        BlockPos pos = new BlockPos(x, y, z);

                        BlockState blockState = world.getBlockState(pos);

                        Block block = blockState.getBlock();

                        for (Colors color : Colors.values()) {
                            if (color.getSpawnpointBlock().equals(block)) {
                                spawnpointCache.get(color).add(pos);
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Teleport players
        for (Colors teamColor : Colors.values()) {
            List<PlayerEntity> team = teamMap.get(teamColor);

            World world = ((MinecraftServer)(Object)this).getOverworld();

            for (PlayerEntity player : team) {
                BlockPos destination = spawnpointCache.get(teamColor).get(0);
                player.requestTeleport(destination.getX() + 0.5, destination.getY() + 1, destination.getZ() + 0.5);
            }
        }

        // Start game

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
        ServerWorld world = ((MinecraftServer)(Object)this).getOverworld();
        ServerEventSending.sendToEveryone(world, NetworkingConstants.GAME_STARTED, PacketByteBufs.empty());
    }
}

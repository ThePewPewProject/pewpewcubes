package de.kleiner3.lasertag.lasertaggame.management.score;

import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Manager to manage the lasertag scores
 *
 * @author Étienne Muser
 */
public class LasertagScoreManager implements IManager {
    private HashMap<UUID, Long> scoreMap = new HashMap<>();

    public Long getScore(UUID uuid) {
        return Optional.ofNullable(scoreMap.get(uuid)).orElse(0L);
    }

    /**
     * Updates the score of the given player
     *
     * @param playerUuid The uuid of the player
     * @param newValue The new score
     */
    public void updateScore(UUID playerUuid, long newValue) {
        scoreMap.put(playerUuid, newValue);
    }

    /**
     * Reset all scores
     *
     * @param world The world the game is in
     */
    public void resetScores(ServerWorld world) {
        scoreMap = new HashMap<>();
        ServerEventSending.sendToEveryone(world, NetworkingConstants.SCORE_RESET, PacketByteBufs.empty());
    }

    public void resetScores() {
        scoreMap = new HashMap<>();
    }

    /**
     * Called when a player scored points
     *
     * @param world The world the game is in
     * @param player The player who scored
     * @param score The score he scored
     */
    public void onPlayerScored(ServerWorld world, PlayerEntity player, long score) {
        increaseScore(world, player.getUuid(), score);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    private void increaseScore(ServerWorld world, UUID uuid, long score) {
        var oldScoreOptional = Optional.ofNullable(scoreMap.get(uuid));

        var newScore = oldScoreOptional.orElse(0L) + score;

        scoreMap.put(uuid, newScore);

        notifyPlayersAboutUpdate(world, uuid, newScore);
    }

    /**
     * Sends a updatedEvent to all clients
     *
     * @param world The world the game is in
     * @param key The UUID of the player whose score changed
     * @param newValue The new score of the player
     */
    private void notifyPlayersAboutUpdate(ServerWorld world, UUID key, Long newValue) {

        var buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeUuid(key);
        buffer.writeLong(newValue);

        ServerEventSending.sendToEveryone(world, NetworkingConstants.SCORE_UPDATE, buffer);
    }
}

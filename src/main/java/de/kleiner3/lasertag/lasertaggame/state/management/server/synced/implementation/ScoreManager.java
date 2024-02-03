package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IScoreManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.IScoreState;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

/**
 * Implementation of the IScoreManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class ScoreManager implements IScoreManager {

    private final ServerWorld world;
    private final IScoreState scoreState;

    public ScoreManager(IScoreState scoreState, ServerWorld world) {
        this.world = world;
        this.scoreState = scoreState;
    }

    @Override
    public long getScore(UUID playerUuuid) {
        return scoreState.getScoreOfPlayer(playerUuuid);
    }

    @Override
    public void resetScores() {
        scoreState.resetScores();
        ServerEventSending.sendToEveryone(world, NetworkingConstants.SCORE_RESET, PacketByteBufs.empty());
    }

    @Override
    public void onPlayerScored(UUID playerUuid, long score) {

        var oldScore = scoreState.getScoreOfPlayer(playerUuid);
        var newScore = oldScore + score;
        scoreState.updateScoreOfPlayer(playerUuid, newScore);

        notifyPlayersAboutUpdate(playerUuid, newScore);
    }

    /**
     * Sends a updatedEvent to all clients
     *
     * @param key The UUID of the player whose score changed
     * @param newValue The new score of the player
     */
    private void notifyPlayersAboutUpdate(UUID key, long newValue) {

        var buffer = new PacketByteBuf(Unpooled.buffer());

        buffer.writeUuid(key);
        buffer.writeLong(newValue);

        ServerEventSending.sendToEveryone(world, NetworkingConstants.SCORE_UPDATE, buffer);
    }
}

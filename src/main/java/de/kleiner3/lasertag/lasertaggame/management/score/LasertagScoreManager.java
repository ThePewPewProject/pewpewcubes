package de.kleiner3.lasertag.lasertaggame.management.score;

import de.kleiner3.lasertag.lasertaggame.management.IManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

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

    public void increaseScore(UUID uuid, long score) {
        var oldScoreOptional = Optional.ofNullable(scoreMap.get(uuid));

        var newScore = oldScoreOptional.orElse(0L) + score;

        scoreMap.put(uuid, newScore);
    }

    public Long getScore(UUID uuid) {
        return Optional.ofNullable(scoreMap.get(uuid)).orElse(0L);
    }

    public void resetScores() {
        scoreMap = new HashMap<>();
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    @Override
    public void syncToClient(ServerPlayerEntity client, MinecraftServer server) {
        // Do not sync!
        throw new UnsupportedOperationException("LasertagScoreManager should not be synced on its own.");
    }
}

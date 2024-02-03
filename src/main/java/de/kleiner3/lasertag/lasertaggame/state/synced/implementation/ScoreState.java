package de.kleiner3.lasertag.lasertaggame.state.synced.implementation;

import de.kleiner3.lasertag.lasertaggame.state.synced.IScoreState;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of IScoreState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class ScoreState implements IScoreState {

    private final HashMap<UUID, Long> scoreMap = new HashMap<>();

    @Override
    public synchronized long getScoreOfPlayer(UUID playerUuid) {
        return Optional.ofNullable(scoreMap.get(playerUuid)).orElse(0L);
    }

    @Override
    public synchronized void updateScoreOfPlayer(UUID playerUuid, long newValue) {
        scoreMap.put(playerUuid, newValue);
    }

    @Override
    public synchronized void resetScores() {
        scoreMap.clear();
    }
}

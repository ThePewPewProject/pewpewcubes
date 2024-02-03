package de.kleiner3.lasertag.lasertaggame.state.management.server.synced;

import java.util.UUID;

/**
 * Interface for a server score manager
 *
 * @author Étienne Muser
 */
public interface IScoreManager {

    /**
     * Get the score of a player
     *
     * @param playerUuuid The uuid of the player
     * @return The players score
     */
    long getScore(UUID playerUuuid);

    /**
     * Reset all scores to zero
     */
    void resetScores();

    /**
     * A player scored
     *
     * @param playerUuid The uuid of the player
     * @param score      The score the player scored
     */
    void onPlayerScored(UUID playerUuid, long score);
}

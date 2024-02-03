package de.kleiner3.lasertag.lasertaggame.state.management.client;

import java.util.UUID;

/**
 * Interface for a client score manager.
 *
 * @author Étienne Muser
 */
public interface IScoreManager {

    /**
     * Get the score of a player
     *
     * @param playerUuid The uuid of the player to get the score from
     * @return The score of the player
     */
    long getScore(UUID playerUuid);

    /**
     * Update the score of a player
     *
     * @param playerUuid The uuid of the player to update the score of
     * @param newValue   The new score of the player
     */
    void updateScore(UUID playerUuid, long newValue);

    /**
     * Reset the scores of all players to zero
     */
    void resetScores();
}

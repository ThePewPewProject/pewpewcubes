package de.kleiner3.lasertag.lasertaggame.state.synced;

import java.util.UUID;

/**
 * Interface for a score state.
 * Resembles the state of what the current score is (What player has what score).
 *
 * @author Étienne Muser
 */
public interface IScoreState {

    /**
     * Get the score of a player
     *
     * @param playerUuid The uuid of the player to get the score of
     * @return The score of the player
     */
    long getScoreOfPlayer(UUID playerUuid);

    /**
     * Update the score of a player
     *
     * @param playerUuid The uuid of the player to update the score of
     * @param newValue   The new score of the player
     */
    void updateScoreOfPlayer(UUID playerUuid, long newValue);

    /**
     * Clear all scores. Set the score of every player to zero.
     */
    void resetScores();
}

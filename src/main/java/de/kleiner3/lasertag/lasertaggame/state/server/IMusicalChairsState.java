package de.kleiner3.lasertag.lasertaggame.state.server;

import java.util.UUID;

/**
 * Interface for a musical chairs state.
 * Resembles the state of a musical chairs game.
 *
 * @author Étienne Muser
 */
public interface IMusicalChairsState {

    /**
     * Set a players overall score
     *
     * @param playerUuid The players uuid
     * @param newScore   The new score of the player
     */
    void setPlayerOverallScore(UUID playerUuid, long newScore);

    /**
     * Get a players overall score
     *
     * @param playerUuid The uuid of the player
     * @return The players overall score
     */
    long getPlayerOverallScore(UUID playerUuid);

    /**
     * Resets the state to pre-game conditions
     */
    void reset();
}

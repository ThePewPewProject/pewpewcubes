package de.kleiner3.lasertag.lasertaggame.state.synced;

import java.util.UUID;

/**
 * Interface for an elimination state.
 * Resembles the state of what player eliminated how many other players and
 * what players are eliminated.
 *
 * @author Étienne Muser
 */
public interface IEliminationState {

    /**
     * Set the elimination count of a player
     *
     * @param playerUuid The uuid of the player
     * @param newCount   The new elimination count of the player
     */
    void setEliminationCount(UUID playerUuid, long newCount);

    /**
     * Get the elimination count of a player
     *
     * @param playerUuid The uuid of the player
     * @return The players elimination count
     */
    long getEliminationCount(UUID playerUuid);

    /**
     * Set a player to be eliminated
     *
     * @param playerUuid The uuid of the player
     */
    void eliminatePlayer(UUID playerUuid);

    /**
     * Check if a player is eliminated
     *
     * @param playerUuid The players uuid
     * @return True if the player is eliminated. Otherwise, false.
     */
    boolean isEliminated(UUID playerUuid);

    /**
     * Reset the state to pre game conditions
     */
    void reset();
}

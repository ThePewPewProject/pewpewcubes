package de.kleiner3.lasertag.lasertaggame.state.management.client;

import java.util.UUID;

/**
 * Interface for a client elimination manager
 *
 * @author Étienne Muser
 */
public interface IEliminationManager {

    /**
     * Get a players elimination count
     *
     * @param playerUuid The players uuid
     * @return The players elimination count
     */
    long getEliminationCount(UUID playerUuid);

    /**
     * Set a players elimination count
     *
     * @param playerUuid       The uuid of the player
     * @param eliminationCount The new elimination count of the player
     */
    void setEliminationCount(UUID playerUuid, long eliminationCount);

    /**
     * Set a player to be eliminated
     *
     * @param playerUuid The uuid of the player
     */
    void eliminatePlayer(UUID playerUuid);

    /**
     * Reset the elimination manager
     */
    void reset();
}

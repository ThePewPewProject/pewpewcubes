package de.pewpewproject.lasertag.lasertaggame.state.management.client;

import java.util.UUID;

/**
 * Interface for a client activation manager.
 *
 * @author Étienne Muser
 */
public interface IActivationManager {

    /**
     * Check whether a player is deactivated
     *
     * @param uuid The uuid of the player
     * @return True if the player is currently deactivated. Otherwise, false.
     */
    boolean isDeactivated(UUID uuid);

    /**
     * Set the deactivation status of a player
     *
     * @param uuid        The uuid of the player to set the deactivation status
     * @param deactivated Flag whether the player is deactivated or not
     */
    void setDeactivated(UUID uuid, boolean deactivated);
}

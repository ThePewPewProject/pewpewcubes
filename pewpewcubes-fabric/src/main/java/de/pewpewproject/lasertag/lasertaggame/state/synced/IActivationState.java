package de.pewpewproject.lasertag.lasertaggame.state.synced;

import java.util.UUID;

/**
 * Interface for an activation state.
 * Resembles the state of what players are currently activated.
 *
 * @author Étienne Muser
 */
public interface IActivationState {
    /**
     * Get if a player is activated
     *
     * @param playerUuid The uuid of the player
     * @return True if the player is currently activated. Otherwise, false.
     */
    boolean isActivated(UUID playerUuid);

    /**
     * Activate a player
     *
     * @param playerUuid The uuid of the player to activate
     */
    void activatePlayer(UUID playerUuid);

    /**
     * Deactivate a player
     *
     * @param playerUuid The uuid of the player to deactivate
     */
    void deactivatePlayer(UUID playerUuid);
}

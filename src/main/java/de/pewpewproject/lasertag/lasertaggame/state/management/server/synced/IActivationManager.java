package de.pewpewproject.lasertag.lasertaggame.state.management.server.synced;

import java.util.UUID;

/**
 * Interface for a server activation manager
 *
 * @author Étienne Muser
 */
public interface IActivationManager {

    /**
     * Check if a player is deactivated
     *
     * @param playerUuid The uuid of the player
     * @return True if the player is deactivated. Otherwise, false.
     */
    boolean isDeactivated(UUID playerUuid);

    /**
     * Deactivate a player for the configured amount of deactivation duration
     *
     * @param playerUuid The uuid of the player to deactivate
     */
    void deactivate(UUID playerUuid);

    /**
     * Deactivate a player for the given amount of deactivation duration
     *
     * @param playerUuid The uuid of the player
     * @param deactivationDuration The deactivation duration in seconds
     */
    void deactivate(UUID playerUuid, long deactivationDuration);

    /**
     * Deactivate all players without reactivating them
     */
    void deactivateAll();

    /**
     * Activate all players
     */
    void activateAll();

    /**
     * Resets the running reactivation threads
     */
    void reset();
}

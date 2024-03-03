package de.pewpewproject.lasertag.lasertaggame.state.synced.implementation;

import de.pewpewproject.lasertag.lasertaggame.state.synced.IActivationState;

import java.util.HashSet;
import java.util.UUID;

/**
 * Implementation of IActivationState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class ActivationState implements IActivationState {

    /**
     * HashSet containing the uuids of all and only the
     * activated players.
     */
    private final HashSet<UUID> activatedPlayersUuids = new HashSet<>();

    @Override
    public synchronized boolean isActivated(UUID playerUuid) {
        return activatedPlayersUuids.contains(playerUuid);
    }

    @Override
    public synchronized void activatePlayer(UUID playerUuid) {
        activatedPlayersUuids.add(playerUuid);
    }

    @Override
    public synchronized void deactivatePlayer(UUID playerUuid) {
        activatedPlayersUuids.remove(playerUuid);
    }
}

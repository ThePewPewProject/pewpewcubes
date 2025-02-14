package de.pewpewproject.lasertag.lasertaggame.state.management.client.implementation;

import de.pewpewproject.lasertag.lasertaggame.state.management.client.IActivationManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IClientLasertagManager;

import java.util.UUID;

/**
 * Implementation of IActivationManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class ActivationManager implements IActivationManager {

    private IClientLasertagManager clientManager;

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public boolean isDeactivated(UUID playerUuid) {
        return !clientManager.getSyncedState().getActivationState().isActivated(playerUuid);
    }

    @Override
    public void setDeactivated(UUID playerUuid, boolean deactivated) {

        if(deactivated) {
            clientManager.getSyncedState().getActivationState().deactivatePlayer(playerUuid);
        } else {
            clientManager.getSyncedState().getActivationState().activatePlayer(playerUuid);
        }
    }
}

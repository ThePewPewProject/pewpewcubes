package de.kleiner3.lasertag.lasertaggame.state.management.client.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.kleiner3.lasertag.lasertaggame.state.management.client.IEliminationManager;

import java.util.UUID;

/**
 * Implementation of IElimination manager for the lasertag game
 *
 * @author Étienne Muser
 */
public class EliminationManager implements IEliminationManager {

    private IClientLasertagManager clientManager;

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public long getEliminationCount(UUID playerUuid) {
        return clientManager.getSyncedState().getEliminationState().getEliminationCount(playerUuid);
    }

    @Override
    public void setEliminationCount(UUID playerUuid, long eliminationCount) {
        clientManager.getSyncedState().getEliminationState().setEliminationCount(playerUuid, eliminationCount);
    }

    @Override
    public void eliminatePlayer(UUID playerUuid) {
        clientManager.getSyncedState().getEliminationState().eliminatePlayer(playerUuid);
    }

    @Override
    public void reset() {
        clientManager.getSyncedState().getEliminationState().reset();
    }
}

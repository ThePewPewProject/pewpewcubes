package de.kleiner3.lasertag.lasertaggame.state.management.client.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.kleiner3.lasertag.lasertaggame.state.management.client.IScoreManager;

import java.util.UUID;

/**
 * Implementation of IScoreManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class ScoreManager implements IScoreManager {

    private IClientLasertagManager clientManager;

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public long getScore(UUID playerUuid) {
        return clientManager.getSyncedState().getScoreState().getScoreOfPlayer(playerUuid);
    }

    @Override
    public void updateScore(UUID playerUuid, long newValue) {
        clientManager.getSyncedState().getScoreState().updateScoreOfPlayer(playerUuid, newValue);
    }

    @Override
    public void resetScores() {
        clientManager.getSyncedState().getScoreState().resetScores();
    }
}

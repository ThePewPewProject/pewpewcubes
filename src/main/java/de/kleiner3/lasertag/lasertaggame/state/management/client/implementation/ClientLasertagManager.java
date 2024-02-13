package de.kleiner3.lasertag.lasertaggame.state.management.client.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.client.*;
import de.kleiner3.lasertag.lasertaggame.state.synced.ISyncedState;

/**
 * Implementation of IClientLasertagManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class ClientLasertagManager implements IClientLasertagManager {

    private ISyncedState syncedState;

    //region Sub-managers

    private final IActivationManager activationManager;

    private final ICaptureTheFlagManager captureTheFlagManager;

    private final IGameModeManager gameModeManager;

    private final IScoreManager scoreManager;

    private final ISettingsManager settingsManager;

    private final ISettingsPresetsNameManager settingsPresetsNameManager;

    private final ITeamsManager teamsManager;

    private final IUIStateManager uiStateManager;

    private final IRemainingTeamsManager remainingTeamsManager;

    //endregion

    public ClientLasertagManager(ISyncedState syncedState,
                                 IActivationManager activationManager,
                                 ICaptureTheFlagManager captureTheFlagManager,
                                 IGameModeManager gameModeManager,
                                 IScoreManager scoreManager,
                                 ISettingsManager settingsManager,
                                 ISettingsPresetsNameManager settingsPresetsNameManager,
                                 ITeamsManager teamsManager,
                                 IUIStateManager uiStateManager,
                                 IRemainingTeamsManager remainingTeamsManager) {
        this.syncedState = syncedState;
        this.activationManager = activationManager;
        this.captureTheFlagManager = captureTheFlagManager;
        this.gameModeManager = gameModeManager;
        this.scoreManager = scoreManager;
        this.settingsManager = settingsManager;
        this.settingsPresetsNameManager = settingsPresetsNameManager;
        this.teamsManager = teamsManager;
        this.uiStateManager = uiStateManager;
        this.remainingTeamsManager = remainingTeamsManager;
    }

    @Override
    public IActivationManager getActivationManager() {
        return activationManager;
    }

    @Override
    public ICaptureTheFlagManager getCaptureTheFlagManager() {
        return captureTheFlagManager;
    }

    @Override
    public IGameModeManager getGameModeManager() {
        return gameModeManager;
    }

    @Override
    public IScoreManager getScoreManager() {
        return scoreManager;
    }

    @Override
    public ISettingsManager getSettingsManager() {
        return settingsManager;
    }

    @Override
    public ISettingsPresetsNameManager getSettingsPresetsNameManager() {
        return settingsPresetsNameManager;
    }

    @Override
    public ITeamsManager getTeamsManager() {
        return teamsManager;
    }

    @Override
    public IUIStateManager getUIStateManager() {
        return uiStateManager;
    }

    @Override
    public ISyncedState getSyncedState() {
        return syncedState;
    }

    @Override
    public IRemainingTeamsManager getRemainingTeamsManager() {
        return remainingTeamsManager;
    }

    @Override
    public void setSyncedState(ISyncedState syncedState) {
        this.syncedState = syncedState;
    }
}

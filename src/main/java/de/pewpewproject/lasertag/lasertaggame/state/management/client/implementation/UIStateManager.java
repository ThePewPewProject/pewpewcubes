package de.pewpewproject.lasertag.lasertaggame.state.management.client.implementation;

import de.pewpewproject.lasertag.common.util.ThreadUtil;
import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IGameModeManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.ISettingsManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IUIStateManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of IUIStateManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class UIStateManager implements IUIStateManager {

    private IClientLasertagManager clientManager;

    private ScheduledExecutorService gameTimer;

    private ScheduledExecutorService preGameTimer;

    private final IGameModeManager gameModeManager;

    private final ISettingsManager settingsManager;

    /**
     * Flag to indicate that the pre game count down of
     * this game has already passed
     */
    private boolean hasPreGamePassed;

    public UIStateManager(IGameModeManager gameModeManager, ISettingsManager settingsManager) {

        this.gameModeManager = gameModeManager;
        this.settingsManager = settingsManager;
        hasPreGamePassed = false;
    }

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    public void startGameTimer(long gameTime) {

        synchronized (this) {

            if (gameTimer != null && !gameTimer.isShutdown()) {
                throw new IllegalStateException("Client gameTimer is already running.");
            }

            clientManager.getSyncedState().getUIState().gameTime = gameTime;

            gameTimer = ThreadUtil.createScheduledExecutor("client-lasertag-game-timer-thread-%d");
            gameTimer.scheduleAtFixedRate(this::gameCountDownTimerTask, 0, 1, TimeUnit.SECONDS);
        }
    }

    public void stopGameTimer() {

        synchronized (this) {

            if (gameTimer == null) {
                return;
            }

            hasPreGamePassed = false;

            gameTimer.shutdownNow();
            gameTimer = null;
            clientManager.getSyncedState().getUIState().gameTime = 0;
        }
    }

    public void startPreGameCountdownTimer(long startingIn) {

        synchronized (this) {

            if (preGameTimer != null && !preGameTimer.isShutdown()) {
                throw new IllegalStateException("Client preGameTimer is already running.");
            }

            clientManager.getSyncedState().getUIState().startingIn = startingIn;

            preGameTimer = ThreadUtil.createScheduledExecutor("client-lasertag-pregame-timer-thread-%d");
            preGameTimer.scheduleAtFixedRate(this::preGameCountDownTimerTask, 1, 1, TimeUnit.SECONDS);
        }
    }

    public void stopPreGameCountdownTimer() {

        synchronized (this) {

            if (preGameTimer == null) {
                return;
            }

            preGameTimer.shutdownNow();
            preGameTimer = null;
            clientManager.getSyncedState().getUIState().startingIn = -1;
        }
    }

    @Override
    public boolean hasPreGamePassed() {
        return hasPreGamePassed;
    }

    //endregion

    //region Private methods

    private void gameCountDownTimerTask() {

        // Increment the game time
        ++clientManager.getSyncedState().getUIState().gameTime;

        if (!gameModeManager.getGameMode().hasInfiniteTime() &&
                (settingsManager.<Long>get(SettingDescription.PLAY_TIME) * 60L) - clientManager.getSyncedState().getUIState().gameTime == 0) {
            stopGameTimer();
        }
    }

    private void preGameCountDownTimerTask() {

        // Decrement the starting in value
        --clientManager.getSyncedState().getUIState().startingIn;

        // If the count down is at the end
        if (clientManager.getSyncedState().getUIState().startingIn <= -1) {

            hasPreGamePassed = true;

            // Start game count down timer from the start
            startGameTimer(0);

            // Stop countdown timer
            stopPreGameCountdownTimer();
        }
    }

    //endregion
}

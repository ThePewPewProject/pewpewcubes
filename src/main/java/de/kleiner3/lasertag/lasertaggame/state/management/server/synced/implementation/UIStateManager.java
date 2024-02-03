package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IGameModeManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IUIStateManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.UIState;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the IUIStateManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class UIStateManager implements IUIStateManager {

    private final UIState uiState;

    private ScheduledExecutorService gameTimer;

    private ScheduledExecutorService preGameTimer;

    private final IGameModeManager gameModeManager;

    private final ISettingsManager settingsManager;

    public UIStateManager(UIState uiState, IGameModeManager gameModeManager, ISettingsManager settingsManager) {

        this.uiState = uiState;
        this.gameModeManager = gameModeManager;
        this.settingsManager = settingsManager;
    }

    public void startGameTimer(long gameTime) {

        synchronized (this) {

            if (gameTimer != null && !gameTimer.isShutdown()) {
                throw new IllegalStateException("Server gameTimer is already running.");
            }

            uiState.gameTime = gameTime;

            gameTimer = ThreadUtil.createScheduledExecutor("server-lasertag-game-timer-thread-%d");
            gameTimer.scheduleAtFixedRate(this::gameCountDownTimerTask, 0, 1, TimeUnit.SECONDS);
        }
    }

    public void stopGameTimer() {

        synchronized (this) {

            if (gameTimer == null) {
                return;
            }

            gameTimer.shutdownNow();
            gameTimer = null;
            uiState.gameTime = 0;
        }
    }

    public void startPreGameCountdownTimer(long startingIn) {

        synchronized (this) {

            if (preGameTimer != null && !preGameTimer.isShutdown()) {
                throw new IllegalStateException("Server preGameTimer is already running.");
            }

            uiState.startingIn = startingIn;

            preGameTimer = ThreadUtil.createScheduledExecutor("server-lasertag-pregame-timer-thread-%d");
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
            uiState.startingIn = -1;
        }
    }

    //endregion

    //region Private methods

    private void gameCountDownTimerTask() {

        // Increment the game time
        ++uiState.gameTime;

        if (!gameModeManager.getGameMode().hasInfiniteTime() &&
                (settingsManager.<Long>get(SettingDescription.PLAY_TIME) * 60L) - uiState.gameTime == 0) {
            stopGameTimer();
        }
    }

    private void preGameCountDownTimerTask() {

        // Decrement the starting in value
        --uiState.startingIn;

        // If the count down is at the end
        if (uiState.startingIn <= -1) {

            // Start game count down timer from the start
            startGameTimer(0);

            // Stop countdown timer
            stopPreGameCountdownTimer();
        }
    }

    //endregion
}

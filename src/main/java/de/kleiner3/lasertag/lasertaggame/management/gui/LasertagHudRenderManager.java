package de.kleiner3.lasertag.lasertaggame.management.gui;

import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.timing.GameCountDownTimerTask;
import de.kleiner3.lasertag.lasertaggame.timing.PreGameCountDownTimerTask;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to hold all lasertag HUD data
 *
 * @author Étienne Muser
 */
public class LasertagHudRenderManager implements IManager {
    //region Public fields

    public boolean shouldRenderNameTags = true;

    /**
     * The time in seconds that has already elapsed
     */
    public long gameTime = 0;

    public double progress = 0.0;

    public long startingIn = -1;

    // The logical size of the window
    public int width;
    public int wMid;
    public int height;
    public int hMid;

    //endregion

    //region Private fields

    private transient ScheduledExecutorService gameTimer;

    private transient ScheduledExecutorService preGameTimer;

    //endregion

    //region Constants

    public static final int progressBarWidth = 100;
    public static final int boxColor = 0x88000000;
    public static final int startY = 10;
    public static final int boxHeight = 65;
    public static final int boxWidth = 85;
    public static final int margin = 20;
    public static final int textPadding = 1;
    public static final int textHeight = 9;

    //endregion

    //region Game timer methods

    public void startGameTimer(long gameTime) {
        if (this.gameTimer != null && !this.gameTimer.isShutdown()) {
            throw new IllegalStateException("this.gameTimer is already running.");
        }

        this.gameTime = gameTime;

        gameTimer = ThreadUtil.createScheduledExecutor("lasertag-game-timer-thread-%d");
        gameTimer.scheduleAtFixedRate(new GameCountDownTimerTask(), 0, 1, TimeUnit.SECONDS);
    }

    public void stopGameTimer() {
        synchronized (this) {
            if (this.gameTimer == null) {
                return;
            }

            gameTimer.shutdownNow();
            this.gameTimer = null;
            this.gameTime = 0;
        }
    }

    public void startPreGameCountdownTimer(long startingIn) {
        if (this.preGameTimer != null && !this.preGameTimer.isShutdown()) {
            throw new IllegalStateException("this.preGameTimer is already running.");
        }

        this.startingIn = startingIn;

        preGameTimer = ThreadUtil.createScheduledExecutor("lasertag-pregame-timer-thread-%d");
        preGameTimer.scheduleAtFixedRate(new PreGameCountDownTimerTask(), 1, 1, TimeUnit.SECONDS);
    }

    public void stopPreGameCountdownTimer() {
        synchronized (this) {
            if (this.preGameTimer == null) {
                return;
            }

            preGameTimer.shutdownNow();
            this.preGameTimer = null;
            this.startingIn = -1;
        }
    }

    //endregion

    public void dispose() {
        // Stop pre game timer if running
        stopPreGameCountdownTimer();

        // Stop game timer if running
        stopGameTimer();
    }
}

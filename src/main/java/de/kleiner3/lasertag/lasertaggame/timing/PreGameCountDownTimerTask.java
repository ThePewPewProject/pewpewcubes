package de.kleiner3.lasertag.lasertaggame.timing;

import de.kleiner3.lasertag.client.hud.LasertagHudOverlay;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Timer task to count down the client side pre game count down
 *
 * @author Étienne Muser
 */
public class PreGameCountDownTimerTask extends TimerTask {
    private final ScheduledExecutorService timer;

    public PreGameCountDownTimerTask(ScheduledExecutorService timer) {
        this.timer = timer;
    }

    @Override
    public void run() {
        --LasertagHudOverlay.renderData.startingIn;

        if (LasertagHudOverlay.renderData.startingIn == -1) {
            timer.shutdown();

            // Start game count down timer
            LasertagHudOverlay.renderData.gameTimer = Executors.newSingleThreadScheduledExecutor();
            LasertagHudOverlay.renderData.gameTimer.scheduleAtFixedRate(new GameCountDownTimerTask(), 0, 1, TimeUnit.SECONDS);
        }
    }
}

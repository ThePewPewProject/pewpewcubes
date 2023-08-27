package de.kleiner3.lasertag.lasertaggame.timing;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;

import java.util.TimerTask;

/**
 * Timer task to count down the client side pre game count down
 *
 * @author Étienne Muser
 */
public class PreGameCountDownTimerTask extends TimerTask {

    @Override
    public void run() {
        var renderData = LasertagGameManager.getInstance().getHudRenderManager();

        --renderData.startingIn;

        if (renderData.startingIn <= -1) {

            // Start game count down timer
            renderData.startGameTimer(0);

            // Stop countdown timer
            renderData.stopPreGameCountdownTimer();
        }
    }
}

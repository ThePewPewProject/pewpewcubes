package de.kleiner3.lasertag.lasertaggame.timing;

import de.kleiner3.lasertag.client.hud.LasertagHudOverlay;

import java.util.TimerTask;

/**
 * Timer task to count down the client side pre game count down
 *
 * @author Étienne Muser
 */
public class PreGameCountDownTimerTask extends TimerTask {

    @Override
    public void run() {
        --LasertagHudOverlay.renderData.startingIn;

        if (LasertagHudOverlay.renderData.startingIn <= -1) {
            // Stop countdown timer
            LasertagHudOverlay.renderData.stopPreGameCountdownTimer();

            // Start game count down timer
            LasertagHudOverlay.renderData.startGameTimer(0);
        }
    }
}

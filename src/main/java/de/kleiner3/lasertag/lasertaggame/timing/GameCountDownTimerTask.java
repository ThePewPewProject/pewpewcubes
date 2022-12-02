package de.kleiner3.lasertag.lasertaggame.timing;

import de.kleiner3.lasertag.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.client.LasertagHudOverlay;
import de.kleiner3.lasertag.settings.SettingNames;

import java.util.TimerTask;

/**
 * Timer task to count down the client side game count down
 *
 * @author Étienne Muser
 */
public class GameCountDownTimerTask extends TimerTask {
    @Override
    public void run() {
        ++LasertagHudOverlay.gameTime;

        if (((int)LasertagSettingsManager.get(SettingNames.PLAY_TIME) * 60L) - LasertagHudOverlay.gameTime == 0) {
            synchronized (LasertagHudOverlay.gameTimerLock) {
                LasertagHudOverlay.gameTimer.shutdown();
                LasertagHudOverlay.gameTimer = null;
                LasertagHudOverlay.gameTime = 0;
            }
        }
    }
}


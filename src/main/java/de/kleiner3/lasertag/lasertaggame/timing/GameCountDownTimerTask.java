package de.kleiner3.lasertag.lasertaggame.timing;

import de.kleiner3.lasertag.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.client.hud.LasertagHudOverlay;
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
        ++LasertagHudOverlay.renderData.gameTime;

        if (((long)LasertagSettingsManager.get(SettingNames.PLAY_TIME) * 60L) - LasertagHudOverlay.renderData.gameTime == 0) {
            synchronized (LasertagHudOverlay.renderData.gameTimerLock) {
                LasertagHudOverlay.renderData.gameTimer.shutdown();
                LasertagHudOverlay.renderData.gameTimer = null;
                LasertagHudOverlay.renderData.gameTime = 0;
            }
        }
    }
}


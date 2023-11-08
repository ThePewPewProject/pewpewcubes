package de.kleiner3.lasertag.lasertaggame.timing;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;

import java.util.TimerTask;

/**
 * Timer task to count down the client side game count down
 *
 * @author Étienne Muser
 */
public class GameCountDownTimerTask extends TimerTask {
    @Override
    public void run() {

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var renderData = gameManager.getHudRenderManager();

        ++renderData.gameTime;

        // Get the game mode
        var gameMode = gameManager.getGameModeManager().getGameMode();

        if (!gameMode.hasInfiniteTime() && (gameManager.getSettingsManager().<Long>get(SettingDescription.PLAY_TIME) * 60L) - renderData.gameTime == 0) {
            renderData.stopGameTimer();
        }
    }
}


package de.kleiner3.lasertag.lasertaggame.timing;

import de.kleiner3.lasertag.lasertaggame.ITickable;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;

import java.util.TimerTask;

/**
 * Timer task to implement the ticking of the game
 *
 * @author Étienne Muser
 */
public class GameTickTimerTask extends TimerTask {
    private final ITickable game;

    private int seconds = -1;

    public GameTickTimerTask(ITickable game) {
        this.game = game;
    }

    @Override
    public void run() {
        ++seconds;

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();

        // Get the game mode
        var gameMode = gameManager.getGameModeManager().getGameMode();

        // If game mode has infinite time
        if (gameMode.hasInfiniteTime()) {

            // Do regular tick every 60 seconds
            if (seconds % 60 == 0) {
                game.doTick(false);
            }
        } else {

            // Get the total duration of the game
            var gameDurationSeconds = gameManager.getSettingsManager().<Long>get(SettingDescription.PLAY_TIME) * 60;

            // If is 30 seconds before end
            if (gameDurationSeconds - seconds == 30) {
                game.thirtySecondsTick();
                return;
            }

            // If is last tick
            if (seconds == gameDurationSeconds) {
                seconds = -1;
                game.endTick();
                return;
            }

            // Else do regular tick every 60 seconds
            if (seconds % 60 == 0) {
                game.doTick(gameDurationSeconds - seconds == 60);
            }
        }
    }
}

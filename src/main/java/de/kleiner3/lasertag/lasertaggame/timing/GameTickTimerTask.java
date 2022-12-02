package de.kleiner3.lasertag.lasertaggame.timing;

import de.kleiner3.lasertag.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.lasertaggame.ITickable;
import de.kleiner3.lasertag.settings.SettingNames;

import java.util.TimerTask;

/**
 * Timer task to implement the ticking of the game
 *
 * @author Étienne Muser
 */
public class GameTickTimerTask extends TimerTask {
    private final ITickable game;

    private int tickNo = -1;

    public GameTickTimerTask(ITickable game) {
        this.game = game;
    }

    @Override
    public void run() {
        ++tickNo;
        game.doTick();

        if (tickNo == (int)LasertagSettingsManager.get(SettingNames.PLAY_TIME)) {
            tickNo = -1;

            game.endTick();
        }
    }
}

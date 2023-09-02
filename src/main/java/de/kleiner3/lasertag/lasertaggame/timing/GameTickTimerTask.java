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

    private int tickNo = -1;

    public GameTickTimerTask(ITickable game) {
        this.game = game;
    }

    @Override
    public void run() {
        ++tickNo;
        game.doTick();

        if (tickNo == LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PLAY_TIME)) {
            tickNo = -1;

            game.endTick();
        }
    }
}

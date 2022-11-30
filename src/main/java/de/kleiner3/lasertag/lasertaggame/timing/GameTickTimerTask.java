package de.kleiner3.lasertag.lasertaggame.timing;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.lasertaggame.ITickable;

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

        if (tickNo == LasertagConfig.getInstance().getPlayTime()) {
            tickNo = -1;

            game.endTick();
        }
    }
}

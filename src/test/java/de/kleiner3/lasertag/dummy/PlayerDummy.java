package de.kleiner3.lasertag.dummy;

import de.kleiner3.lasertag.lasertaggame.ILasertagPlayer;

/**
 * Dummy implementation of ILasertagPlayer for unit testing
 *
 * @author Étienne Muser
 */
public class PlayerDummy implements ILasertagPlayer {
    private String name;
    private int score;

    public PlayerDummy(String name, int score) {
        this.name = name;
        this.score = score;
    }
    @Override
    public String getLasertagUsername() {
        return this.name;
    }

    @Override
    public int getLasertagScore() {
        return this.score;
    }
}

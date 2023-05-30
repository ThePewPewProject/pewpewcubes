package de.kleiner3.lasertag.lasertaggame;

/**
 * Interface for everything that can be ticked by an external timer
 *
 * @author Étienne Muser
 */
public interface ITickable {

    /**
     * Called on every tick
     */
    void doTick();

    /**
     * Called on the last tick
     */
    void endTick();
}

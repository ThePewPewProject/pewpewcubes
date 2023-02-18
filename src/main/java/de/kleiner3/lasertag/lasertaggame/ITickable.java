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
    default void doTick() {
        // Default empty
    }

    /**
     * Called on the last tick
     */
    default void endTick() {
        // Default empty
    }
}

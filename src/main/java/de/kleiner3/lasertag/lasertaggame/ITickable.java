package de.kleiner3.lasertag.lasertaggame;

/**
 * Interface for everything that can be ticked by an external timer
 *
 * @author Étienne Muser
 */
public interface ITickable {

    /**
     * Called on every tick
     *
     * @param isLastNormalTick Flag to indicate whether this tick is the last normal tick
     */
    void doTick(boolean isLastNormalTick);

    /**
     * Called on the last tick
     */
    void endTick();

    /**
     * Called 30 seconds before end
     */
    void thirtySecondsTick();
}

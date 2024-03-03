package de.pewpewproject.lasertag.lasertaggame;

/**
 * Interface providing methods for a lasertag player
 *
 * @author Étienne Muser
 */
public interface ILasertagPlayer {

    /**
     * Called when the player is deactivated
     */
    default void onDeactivated() {
        // Default empty
    }

    /**
     * Called when the player gets activated
     */
    default void onActivated() {
        // Default empty
    }

    default String getLasertagUsername() {
        return null;
    }
}

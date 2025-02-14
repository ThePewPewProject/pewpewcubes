package de.pewpewproject.lasertag.lasertaggame.state.management.client;

/**
 * Interface for a client ui state manager.
 *
 * @author Étienne Muser
 */
public interface IUIStateManager {

    /**
     * Start the game timer
     *
     * @param gameTime The initial value for the game time
     */
    void startGameTimer(long gameTime);

    /**
     * Stop the game timer
     */
    void stopGameTimer();

    /**
     * Get if the pre game count down is over
     *
     * @return True if the pre game count down is over and a game is running. Otherwise, false.
     */
    boolean hasPreGamePassed();

    /**
     * Start the pre game count down timer
     *
     * @param startingIn The initial value for the time left in the count down
     */
    void startPreGameCountdownTimer(long startingIn);

    /**
     * Stop the pre game count down timer
     */
    void stopPreGameCountdownTimer();
}

package de.kleiner3.lasertag.lasertaggame.state.management.server.synced;

/**
 * Interface for a server ui state manager
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
     * Start the pre-game count down timer
     *
     * @param startingIn The initial value for the starting in
     */
    void startPreGameCountdownTimer(long startingIn);

    /**
     * Stop the pre-game count down timer
     */
    void stopPreGameCountdownTimer();
}

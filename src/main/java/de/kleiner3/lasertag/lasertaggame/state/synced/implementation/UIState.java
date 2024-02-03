package de.kleiner3.lasertag.lasertaggame.state.synced.implementation;

/**
 * The state of the UI.
 *
 * @author Étienne Muser
 */
public class UIState {

    //region Constants

    /**
     * The logical width of progressbars.
     */
    public static final int progressBarWidth = 100;

    /**
     * The background color of boxes.
     */
    public static final int boxColor = 0x88000000;

    //endregion

    //region Fields

    /**
     * The time in seconds that has already elapsed in this game.
     */
    public long gameTime = 0;

    /**
     * Flag to indicate whether a game is running or not.
     */
    public boolean isGameRunning = false;

    /**
     * The progress of an action. Value is in range [0, 1] or -1.
     * If the value is -1, then no progress bar will be
     * displayed. Otherwise, a progressbar of this percentage
     * will be displayed.
     * Used in the in-game overlay.
     */
    public double progress = -1.0;

    /**
     * An information string that will be displayed beside
     * the progress bar in the map loading screen. This
     * text is only shown if the mapLoadingProgress is not -1.
     */
    public String mapLoadingStepString = "";

    /**
     * The progress of the current map loading step. Value is
     * in range [0, 1] or -1. If the value is -1, then not
     * progress bar will be displayed. Otherwise, a progressbar
     * of this percentage will be displayed.
     * Used in the map loading screen.
     */
    public double mapLoadingProgress = -1.0;

    /**
     * The time in seconds that are left in the pre-game count
     * down. If the value is -1, then no count down will be
     * displayed. If the value is 0, then "GO" will be displayed
     * if the value is any other value, then this number will
     * be displayed.
     */
    public long startingIn = -1;

    /**
     * The team id of the winning team in the last game.
     */
    public int lastGameWinnerId = -1;

    //endregion
}

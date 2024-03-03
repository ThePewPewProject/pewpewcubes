package de.pewpewproject.lasertag.lasertaggame.state.management.client;

import de.pewpewproject.lasertag.lasertaggame.gamemode.GameMode;

/**
 * Interface for a client game mode manager.
 *
 * @author Étienne Muser
 */
public interface IGameModeManager {

    /**
     * Set the current game mode
     *
     * @param newGameMode The new game mode
     */
    void setGameMode(GameMode newGameMode);

    /**
     * Get the currently selected game mode
     *
     * @return The current game mode
     */
    GameMode getGameMode();
}

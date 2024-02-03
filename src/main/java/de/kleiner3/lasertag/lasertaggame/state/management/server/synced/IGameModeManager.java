package de.kleiner3.lasertag.lasertaggame.state.management.server.synced;

import de.kleiner3.lasertag.lasertaggame.gamemode.GameMode;

/**
 * Interface for a server game mode manager
 *
 * @author Étienne Muser
 */
public interface IGameModeManager {

    /**
     * Set the game mode
     *
     * @param newGameMode The new game mode
     */
    void setGameMode(GameMode newGameMode);

    /**
     * Get the current game mode
     *
     * @return The currently selected game mode
     */
    GameMode getGameMode();
}

package de.pewpewproject.lasertag.lasertaggame.state.management.client.implementation;

import de.pewpewproject.lasertag.lasertaggame.gamemode.GameMode;
import de.pewpewproject.lasertag.lasertaggame.gamemode.GameModes;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IClientLasertagManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IGameModeManager;

/**
 * Implementation of IGameModeManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class GameModeManager implements IGameModeManager {

    private IClientLasertagManager clientManager;

    public void setClientManager(IClientLasertagManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void setGameMode(GameMode newGameMode) {
        clientManager.getSyncedState().getGameModeState().currentGameModeTranslatableName = newGameMode.getTranslatableName();
    }

    @Override
    public GameMode getGameMode() {
        return GameModes.GAME_MODES.get(clientManager.getSyncedState().getGameModeState().currentGameModeTranslatableName);
    }
}

package de.pewpewproject.lasertag.lasertaggame.state.synced;

import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.GameModeState;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.SettingsState;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.UIState;

/**
 * Interface for a synced state.
 * The state that holds all sub-states and gets synchronized to the clients.
 *
 * @author Étienne Muser
 */
public interface ISyncedState {

    IActivationState getActivationState();

    ICaptureTheFlagState getCaptureTheFlagState();

    GameModeState getGameModeState();

    IPlayerNamesState getPlayerNamesState();

    IScoreState getScoreState();

    ISettingsPresetsNamesState getSettingsPresetsNamesState();

    SettingsState getSettingsState();

    ITeamsState getTeamsState();

    ITeamsConfigState getTeamsConfigState();

    UIState getUIState();

    IEliminationState getEliminationState();

    ILasertargetState getLasertargetState();

    /**
     * Serialize the entire synced state to JSON
     *
     * @return A string containing a JSON representation of the synced state
     */
    String toJson();
}

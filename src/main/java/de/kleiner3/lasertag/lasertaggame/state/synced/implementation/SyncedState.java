package de.kleiner3.lasertag.lasertaggame.state.synced.implementation;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.kleiner3.lasertag.lasertaggame.state.synced.*;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import de.kleiner3.lasertag.lasertaggame.team.serialize.TeamConfigManagerDeserializer;
import de.kleiner3.lasertag.lasertaggame.team.serialize.TeamDtoSerializer;

import java.util.HashMap;

/**
 * Implementation of ISyncedState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class SyncedState implements ISyncedState {

    //region The sub-states

    private final IActivationState activationState;
    private final ICaptureTheFlagState captureTheFlagState;
    private final GameModeState gameModeState;
    private final IPlayerNamesState playerNamesState;
    private final IScoreState scoreState;
    private final ISettingsPresetsNamesState settingsPresetsNamesState;
    private final SettingsState settingsState;
    private final ITeamsState teamsState;
    private final ITeamsConfigState teamsConfigState;
    private final UIState uiState;
    private final IEliminationState eliminationState;
    private final ILasertargetState lasertargetState;

    //endregion

    public SyncedState() {

        activationState = new ActivationState();
        captureTheFlagState = new CaptureTheFlagState();
        gameModeState = new GameModeState();
        playerNamesState = new PlayerNamesState();
        scoreState = new ScoreState();
        settingsPresetsNamesState = new SettingsPresetsNamesState();
        settingsState = new SettingsState();
        teamsConfigState = new TeamsConfigState();
        teamsState = new TeamsState(teamsConfigState);
        uiState = new UIState();
        eliminationState = new EliminationState();
        lasertargetState = new LasertargetState();
    }

    //region Public methods

    @Override
    public IActivationState getActivationState() {
        return activationState;
    }

    @Override
    public ICaptureTheFlagState getCaptureTheFlagState() {
        return captureTheFlagState;
    }

    @Override
    public GameModeState getGameModeState() {
        return gameModeState;
    }

    @Override
    public IPlayerNamesState getPlayerNamesState() {
        return playerNamesState;
    }

    @Override
    public IScoreState getScoreState() {
        return scoreState;
    }

    @Override
    public ISettingsPresetsNamesState getSettingsPresetsNamesState() {
        return settingsPresetsNamesState;
    }

    @Override
    public SettingsState getSettingsState() {
        return settingsState;
    }

    @Override
    public ITeamsState getTeamsState() {
        return teamsState;
    }

    @Override
    public ITeamsConfigState getTeamsConfigState() {
        return teamsConfigState;
    }

    @Override
    public UIState getUIState() {
        return uiState;
    }

    @Override
    public IEliminationState getEliminationState() {
        return eliminationState;
    }

    @Override
    public ILasertargetState getLasertargetState() {
        return lasertargetState;
    }

    //endregion


    @Override
    public String toJson() {
        var builder = new GsonBuilder();

        // Register team serializer
        builder.registerTypeAdapter(TeamDto.class, TeamDtoSerializer.getSerializer());

        addTypeAdapterFactoriesForSubStates(builder);

        return builder.enableComplexMapKeySerialization().create().toJson(this);
    }

    public static SyncedState fromJson(String jsonString) {

        var builder = new GsonBuilder();

        // Set number strategy
        builder.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE);

        // Register team serializer
        builder.registerTypeAdapter(new TypeToken<HashMap<String, TeamDto>>() {}.getType(), TeamConfigManagerDeserializer.getDeserializer());

        addTypeAdapterFactoriesForSubStates(builder);

        return builder.create().fromJson(jsonString, SyncedState.class);
    }

    private static void addTypeAdapterFactoriesForSubStates(GsonBuilder builder) {
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(IActivationState.class).registerSubtype(ActivationState.class));
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(ICaptureTheFlagState.class).registerSubtype(CaptureTheFlagState.class));
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(IPlayerNamesState.class).registerSubtype(PlayerNamesState.class));
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(IScoreState.class).registerSubtype(ScoreState.class));
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(ISettingsPresetsNamesState.class).registerSubtype(SettingsPresetsNamesState.class));
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(ITeamsState.class).registerSubtype(TeamsState.class));
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(ITeamsConfigState.class).registerSubtype(TeamsConfigState.class));
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(IEliminationState.class).registerSubtype(EliminationState.class));
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(ILasertargetState.class).registerSubtype(LasertargetState.class));
    }
}

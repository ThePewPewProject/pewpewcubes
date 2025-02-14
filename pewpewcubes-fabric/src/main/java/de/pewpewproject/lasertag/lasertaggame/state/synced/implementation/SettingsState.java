package de.pewpewproject.lasertag.lasertaggame.state.synced.implementation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import de.pewpewproject.lasertag.lasertaggame.gamemode.GameModes;
import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.state.synced.ISettingsState;

import java.util.*;

/**
 * The state of the settings.
 *
 * @author Étienne Muser
 */
public class SettingsState implements ISettingsState {

    private final Map<String, Map<String, Object>> settings;

    private transient final Set<String> generalSettingNames;

    public SettingsState() {

        // Get the game modes
        var gameModes = GameModes.GAME_MODES;

        // Get the setting descriptions
        var settingDescriptions = SettingDescription.values();

        // Create the settings map
        settings = new HashMap<>(gameModes.size());

        // Create the set of general setting names
        generalSettingNames = new HashSet<>();

        // Fill the settings map with the maps for every game mode
        gameModes.keySet().forEach(gm -> settings.put(gm, new HashMap<>(settingDescriptions.length)));

        // For every game mode and setting
        gameModes.keySet().forEach(gm -> Arrays.stream(settingDescriptions).forEach(sd -> {
            if (sd.getDataType().isEnum()) {
                settings.get(gm)
                        .put(sd.getName(), ((Enum<?>)sd.getDefaultValue()).name());
            } else {
                settings.get(gm)
                        .put(sd.getName(), sd.getDefaultValue());
            }

            if (sd.isGeneral()) {
                generalSettingNames.add(sd.getName());
            }
        }));

        // Overwrite the overwritten settings
        gameModes.forEach((gameModeName, gameMode) ->
                gameMode.getOverwrittenSettings().forEach(s ->
                        settings.get(gameModeName).put(s.x().getName(), s.y())));
    }

    @Override
    public Object get(String gameMode, String settingName) {
        return settings.get(gameMode).get(settingName);
    }

    @Override
    public void set(String gameMode, String settingName, Object newValue) {
        settings.get(gameMode).put(settingName, newValue);

        // If the setting is a general setting
        if (generalSettingNames.contains(settingName)) {
            // Set the setting in all game modes
            GameModes.GAME_MODES.keySet().forEach(gm -> settings.get(gm).put(settingName, newValue));
        }
    }

    @Override
    public void fillWith(ISettingsState other) {

        // Copy values of other settings state into this settings state
        settings.keySet().forEach(gm ->
                settings.get(gm).keySet().forEach(sn ->
                        settings.get(gm).put(sn, other.get(gm, sn))));
    }

    @Override
    public boolean contains(String gameMode, String settingName) {
        return settings.containsKey(gameMode) && settings.get(gameMode).containsKey(settingName);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static SettingsState fromJson(String json) {
        return new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .create()
                .fromJson(json, SettingsState.class);
    }
}

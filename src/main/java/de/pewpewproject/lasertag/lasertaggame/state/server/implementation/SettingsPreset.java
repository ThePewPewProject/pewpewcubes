package de.pewpewproject.lasertag.lasertaggame.state.server.implementation;

import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.state.synced.ISettingsState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A single settings preset
 *
 * @author Étienne Muser
 */
public class SettingsPreset {

    /**
     * The game mode of the preset
     */
    private final String gameMode;

    /**
     * The setting values of the preset
     *     key:   The settings name
     *     value: The settings value
     */
    private final Map<String, Object> settings;

    public SettingsPreset(String gameMode) {
        this.gameMode = gameMode;
        settings = new HashMap<>(SettingDescription.values().length);
    }

    /**
     * Get the game mode name of this setting
     *
     * @return The game modes name as a string
     */
    public String getGameMode() {
        return gameMode;
    }

    /**
     * Put a value for a setting into this preset
     *
     * @param key   The settings name
     * @param value The settings value
     */
    public void put(String key, Object value) {
        settings.put(key, value);
    }

    /**
     * Execute a given action for every setting name, setting value pair
     *
     * @param action The action to execute
     */
    public void forEach(BiConsumer<String, Object> action) {
        settings.forEach(action);
    }

    /**
     * Convert a settings state into a preset
     *
     * @param settings     The settings to convert
     * @param gameModeName The name of the current game mode
     * @return The created preset
     */
    public static SettingsPreset fromSettings(ISettingsState settings, String gameModeName) {

        var preset = new SettingsPreset(gameModeName);

        Arrays.stream(SettingDescription.values()).forEach(s -> preset.put(s.getName(), settings.get(gameModeName, s.getName())));

        return preset;
    }
}

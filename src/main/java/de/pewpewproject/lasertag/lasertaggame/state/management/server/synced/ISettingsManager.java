package de.pewpewproject.lasertag.lasertaggame.state.management.server.synced;

import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.state.server.implementation.SettingsPreset;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.SettingsState;

/**
 * Interface for a server settings manager
 *
 * @author Étienne Muser
 */
public interface ISettingsManager {

    /**
     * Get the value of a setting
     *
     * @param setting The setting description of the setting
     * @param <T>     The data type of the setting
     * @return The value of the setting
     */
    <T> T get(SettingDescription setting);

    /**
     * Get the enum value of a setting
     *
     * @param setting The setting description of the setting
     * @param <T>     The enum data type of the setting
     * @return The value of the setting
     */
    <T extends Enum<T>> T getEnum(SettingDescription setting);

    /**
     * Set the value of a specific setting
     *
     * @param key   The name of the setting
     * @param value The new value of the setting
     */
    void set(String key, Object value);

    /**
     * Load a given preset
     *
     * @param preset The preset to load
     */
    void set(SettingsPreset preset);

    /**
     * Set all settings
     *
     * @param newSettings The new settings
     */
    void set(SettingsState newSettings);

    /**
     * Reset all settings to their default values specified by the currently selected game mode
     */
    void reset();

    /**
     * Reset a setting to its default value specified by the currently selected game mode
     *
     * @param settingName The name of the setting
     */
    void reset(String settingName);
}

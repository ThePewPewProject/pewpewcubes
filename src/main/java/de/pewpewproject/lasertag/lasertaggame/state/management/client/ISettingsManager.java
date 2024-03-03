package de.pewpewproject.lasertag.lasertaggame.state.management.client;

import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;

/**
 * Interface for a client settings manager.
 *
 * @author Étienne Muser
 */
public interface ISettingsManager {

    /**
     * Get the value of a setting
     *
     * @param setting The setting description of the setting to get the value from
     * @param <T>     The data type of the setting
     * @return The value of the setting
     */
    <T> T get(SettingDescription setting);

    /**
     * Get the value of an enum setting
     *
     * @param setting The setting description of the setting to get the value from
     * @param <T>     The enum data type of the setting
     * @return The value of the setting
     */
    <T extends Enum<T>> T getEnum(SettingDescription setting);

    /**
     * Set all settings from a JSON string
     *
     * @param newSettingsJson The new settings as JSON
     */
    void set(String newSettingsJson);

    /**
     * Set the value of a specific setting
     *
     * @param settingName The name of the setting
     * @param newValue    The new value of the setting
     */
    void set(String settingName, Object newValue);
}

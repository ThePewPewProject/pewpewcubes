package de.pewpewproject.lasertag.lasertaggame.state.synced;

/**
 * Interface for a settings state
 *
 * @author Étienne Muser
 */
public interface ISettingsState {

    /**
     * Get the value of a setting
     *
     * @param gameMode    The game mode for which to get the value
     * @param settingName The setting name
     * @return The settings value
     */
    Object get(String gameMode, String settingName);

    /**
     * Set a settings value
     *
     * @param gameMode    The game mode to set the setting value for
     * @param settingName The settings name
     * @param newValue    The new value of the setting
     */
    void set(String gameMode, String settingName, Object newValue);

    /**
     * Fill the setting state with the values of another setting state
     *
     * @param other The other setting state
     */
    void fillWith(ISettingsState other);

    /**
     * Check if a setting is contained for a given game mode
     *
     * @param gameMode    The game mode to check for
     * @param settingName The setting name to check for
     * @return True if the setting exists in the given game mode. Otherwise, false.
     */
    boolean contains(String gameMode, String settingName);

    /**
     * Convert the settings state to a json string
     *
     * @return The json representation of this state in a string
     */
    String toJson();
}

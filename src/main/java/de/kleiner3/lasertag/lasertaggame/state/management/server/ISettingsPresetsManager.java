package de.kleiner3.lasertag.lasertaggame.state.management.server;

import de.kleiner3.lasertag.lasertaggame.state.server.implementation.SettingsPresetsState;

/**
 * Interface for a server settings presets manager
 *
 * @author Étienne Muser
 */
public interface ISettingsPresetsManager {

    /**
     * Get the presets
     *
     * @return The settings presets state
     */
    SettingsPresetsState getPresets();

    /**
     * Save the current settings as a new preset
     *
     * @param name The name of the new preset
     */
    void savePreset(String name);

    /**
     * Load a preset into the settings
     *
     * @param name The name of the preset to load
     * @return True if the preset was found. Otherwise, false.
     */
    boolean loadPreset(String name);

    /**
     * Delete a preset name
     *
     * @param name The name of the preset to delete
     * @return True if the preset was found. Otherwise, false.
     */
    boolean deletePreset(String name);
}

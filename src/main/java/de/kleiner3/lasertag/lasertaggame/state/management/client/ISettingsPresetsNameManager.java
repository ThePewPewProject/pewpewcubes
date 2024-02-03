package de.kleiner3.lasertag.lasertaggame.state.management.client;

import java.util.List;

/**
 * Interface for a client settings presets name manager.
 *
 * @author Étienne Muser
 */
public interface ISettingsPresetsNameManager {

    /**
     * Save a preset name
     *
     * @param name The name of the preset
     */
    void addPresetName(String name);

    /**
     * Remove a preset name
     *
     * @param name The name of the preset
     */
    void removePresetName(String name);

    /**
     * Get all preset names
     *
     * @return A list containing all preset names
     */
    List<String> getSettingsPresetNames();
}

package de.kleiner3.lasertag.lasertaggame.state.management.server.synced;

import java.util.List;

/**
 * Interface for a server settings presets name manager
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
     * @param name The preset name to remove
     */
    void removePresetName(String name);

    /**
     * Get all saved preset names
     *
     * @return List containing all preset names
     */
    List<String> getSettingsPresetNames();
}

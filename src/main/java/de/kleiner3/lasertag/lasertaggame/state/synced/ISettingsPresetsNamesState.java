package de.kleiner3.lasertag.lasertaggame.state.synced;

import java.util.List;

/**
 * Interface for a settings presets names state.
 * Resembles the state of what preset names exist.
 *
 * @author Étienne Muser
 */
public interface ISettingsPresetsNamesState {

    /**
     * Add a preset name
     *
     * @param name The name to add
     */
    void addPresetName(String name);

    /**
     * Remove a preset name
     *
     * @param name The name to remove
     */
    void removePresetName(String name);

    /**
     * Get all preset names
     *
     * @return A list containing all preset names
     */
    List<String> getAllPresetNames();
}

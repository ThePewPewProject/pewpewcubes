package de.pewpewproject.lasertag.lasertaggame.state.synced.implementation;

import de.pewpewproject.lasertag.lasertaggame.state.synced.ISettingsPresetsNamesState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of ISettingsPresetsNamesState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class SettingsPresetsNamesState implements ISettingsPresetsNamesState {

    private final Set<String> settingsPresetNames = new HashSet<>();


    @Override
    public void addPresetName(String name) {
        settingsPresetNames.add(name);
    }

    @Override
    public void removePresetName(String name) {
        settingsPresetNames.remove(name);
    }

    @Override
    public List<String> getAllPresetNames() {
        return settingsPresetNames.stream().toList();
    }
}

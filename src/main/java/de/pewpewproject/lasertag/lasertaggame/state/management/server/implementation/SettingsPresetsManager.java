package de.pewpewproject.lasertag.lasertaggame.state.management.server.implementation;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.ISettingsPresetsManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.ISettingsPresetsNameManager;
import de.pewpewproject.lasertag.lasertaggame.state.server.implementation.SettingsPresetsState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Implementation of ISettingsPresetsManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class SettingsPresetsManager implements ISettingsPresetsManager {

    //region Private fields
    private final SettingsPresetsState state;

    // Get path to lasertag settings file
    private static final Path lasertagSettingsPresetsFilePath = LasertagMod.configFolderPath.resolve("lasertagSettingsPresets.json");

    private final ISettingsManager settingsManager;

    //endregion

    public SettingsPresetsManager(SettingsPresetsState state,
                                  ISettingsPresetsNameManager settingsPresetsNamesManager,
                                  ISettingsManager settingsManager) {

        this.state = state;
        this.settingsManager = settingsManager;

        if (Files.exists(lasertagSettingsPresetsFilePath)) {

            try {
                var presetsFileContents = Files.readString(lasertagSettingsPresetsFilePath);

                state = SettingsPresetsState.fromJson(presetsFileContents);
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Reading of lasertag settings presets file failed: " + e.getMessage());
            }
        } else {
            state = SettingsPresetsState.createNewPresetsMap();
            persist();
        }

        if (state != null) {
            state.keySet().forEach(settingsPresetsNamesManager::addPresetName);
        }
    }

    @Override
    public SettingsPresetsState getPresets() {
        return state;
    }

    @Override
    public void savePreset(String name) {
        state.put(name, settingsManager.cloneSettings());
        persist();
    }

    @Override
    public boolean loadPreset(String name) {

        if (!state.containsKey(name)) {
            return false;
        }

        var preset = state.get(name);
        settingsManager.set(preset);
        return true;
    }

    @Override
    public boolean deletePreset(String name) {

        if (!state.containsKey(name)) {
            return false;
        }

        state.remove(name);
        persist();
        return true;
    }

    private void persist() {
        try {
            Files.createDirectories(lasertagSettingsPresetsFilePath.getParent());
            Files.writeString(lasertagSettingsPresetsFilePath, state.toJson(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            LasertagMod.LOGGER.error("Could not persist preset: " + ex.getMessage());
        }
    }
}

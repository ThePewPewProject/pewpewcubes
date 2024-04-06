package de.pewpewproject.lasertag.lasertaggame.state.management.server.implementation;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.lasertaggame.gamemode.GameModes;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.ISettingsPresetsManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.IGameModeManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.ISettingsPresetsNameManager;
import de.pewpewproject.lasertag.lasertaggame.state.server.implementation.SettingsPreset;
import de.pewpewproject.lasertag.lasertaggame.state.server.implementation.SettingsPresetsState;
import de.pewpewproject.lasertag.lasertaggame.state.synced.ISettingsState;

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

    private final ISettingsState settingsState;

    // Get path to lasertag settings file
    private static final Path lasertagSettingsPresetsFilePath = LasertagMod.configFolderPath.resolve("lasertagSettingsPresets.json");

    private final ISettingsManager settingsManager;

    private final IGameModeManager gameModeManager;

    //endregion

    public SettingsPresetsManager(ISettingsState settingsState,
                                  ISettingsPresetsNameManager settingsPresetsNamesManager,
                                  ISettingsManager settingsManager,
                                  IGameModeManager gameModeManager) {

        this.settingsState = settingsState;
        this.settingsManager = settingsManager;
        this.gameModeManager = gameModeManager;

        SettingsPresetsState presetsState = null;

        if (Files.exists(lasertagSettingsPresetsFilePath)) {

            try {
                var presetsFileContents = Files.readString(lasertagSettingsPresetsFilePath);

                presetsState = SettingsPresetsState.fromJson(presetsFileContents);
            } catch (Exception e) {
                LasertagMod.LOGGER.warn("Reading of lasertag settings presets file failed: " + e.getMessage());
            }
        }

        if (presetsState == null) {
            presetsState = SettingsPresetsState.createNewPresetsMap();
        }

        state = presetsState;

        persist();

        state.keySet().forEach(settingsPresetsNamesManager::addPresetName);
    }

    @Override
    public SettingsPresetsState getPresets() {
        return state;
    }

    @Override
    public void savePreset(String name) {
        state.put(name, SettingsPreset.fromSettings(settingsState, gameModeManager.getGameMode().getTranslatableName()));
        persist();
    }

    @Override
    public boolean loadPreset(String name) {

        if (!state.containsKey(name)) {
            return false;
        }

        // Get the preset
        var preset = state.get(name);

        // Get the game mode of the preset
        var gameMode = GameModes.GAME_MODES.get(preset.getGameMode());

        // Set the game mode
        gameModeManager.setGameMode(gameMode);

        // Load the settings
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

package de.pewpewproject.pewpewcubes.core.usecases.intializeserver

import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import de.pewpewproject.pewpewcubes.constants.LASERTAG_SETTINGS_PRESETS_FILE_NAME
import de.pewpewproject.pewpewcubes.entities.settings.SettingsPreset
import de.pewpewproject.pewpewcubes.entities.state.PewPewCubesServerState
import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap

/**
 * Use case to initialize the server settings presets
 * @author Étienne Muser
 */
class InitializeServerSettingsPresetsUseCase(private val configFolderPath: Path) {
    operator fun invoke(serverId: String) {
        // Get the server state
        val state = PewPewCubesServerState.getInstance(serverId)

        // Get the path to the lasertag settings presets file
        val lasertagSettingsPresetsFilePath = configFolderPath.resolve(LASERTAG_SETTINGS_PRESETS_FILE_NAME)

        // Read the settings presets file
        var settingsPresetsFileContents = Files.readString(lasertagSettingsPresetsFilePath)

        // Create a type token for the presets
        var settingsPresetsMapType = object : TypeToken<HashMap<String, SettingsPreset>>() {}.type

        // Parse the settings preset file
        val settingsPresets = GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create()
            .fromJson<HashMap<String, SettingsPreset>>(settingsPresetsFileContents, settingsPresetsMapType)

        // Set the settings presets
        state.settingsPresets = settingsPresets
    }
}
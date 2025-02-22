package de.pewpewproject.pewpewcubes.core.usecases.intializeserver

import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.google.gson.reflect.TypeToken
import de.pewpewproject.pewpewcubes.constants.LASERTAG_SETTINGS_FILE_NAME
import de.pewpewproject.pewpewcubes.entities.GameMode
import de.pewpewproject.pewpewcubes.entities.settings.SettingDescription
import de.pewpewproject.pewpewcubes.entities.state.PewPewCubesServerState
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/**
 * Use case to initialize the server settings state
 * @author Étienne Muser
 */
class InitializeServerSettingsStateUseCase(private val configFolderPath: Path) {
    operator fun invoke(serverId: String) {
        // Get the server state
        val state = PewPewCubesServerState.getInstance(serverId)

        // Get the path to the lasertag settings file
        val lasertagSettingsFilePath = configFolderPath.resolve(LASERTAG_SETTINGS_FILE_NAME)

        // Read the settings file
        var settingsFileContents = Files.readString(lasertagSettingsFilePath)

        // Create a type token for the map
        val settingsMapTypeToken = object : TypeToken<EnumMap<GameMode, EnumMap<SettingDescription, Any>>>() {}.type

        // Parse the settings file
        val settingsMap = GsonBuilder()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create()
            .fromJson<EnumMap<GameMode, EnumMap<SettingDescription, Any>>>(
                settingsFileContents, settingsMapTypeToken
            )

        // Set the settings map
        state.gameState.settings = settingsMap
    }
}
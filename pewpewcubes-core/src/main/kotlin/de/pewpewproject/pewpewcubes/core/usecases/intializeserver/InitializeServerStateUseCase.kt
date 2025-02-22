package de.pewpewproject.pewpewcubes.core.usecases.intializeserver

import de.pewpewproject.pewpewcubes.entities.state.PewPewCubesServerState
import java.nio.file.Path


/**
 * Use case to initialize the server state
 * @author Étienne Muser
 */
class InitializeServerStateUseCase(private val configFolderPath: Path) {

    operator fun invoke(serverId: String) {
        // Initialize the game mode
        initializeGameModeUseCase(serverId)

        // Initialize the settings
        initializeSettingsUseCase(serverId)

        // Initialize the settings presets
        initializeSettingsPresetsUseCase(serverId)

        // Get the server state
        val state = PewPewCubesServerState.getInstance(serverId)

        // Set the state initialized
        state.initialized = true
    }

    private val initializeGameModeUseCase = InitializeServerGameModeUseCase(configFolderPath)
    private val initializeSettingsUseCase = InitializeServerSettingsStateUseCase(configFolderPath)
    private val initializeSettingsPresetsUseCase = InitializeServerSettingsPresetsUseCase(configFolderPath)
}
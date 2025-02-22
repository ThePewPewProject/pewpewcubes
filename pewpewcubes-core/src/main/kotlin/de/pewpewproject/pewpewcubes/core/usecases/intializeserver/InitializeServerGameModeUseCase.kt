package de.pewpewproject.pewpewcubes.core.usecases.intializeserver

import de.pewpewproject.pewpewcubes.constants.LASERTAG_GAME_MODE_FILE_NAME
import de.pewpewproject.pewpewcubes.core.exceptions.ConfigurationException
import de.pewpewproject.pewpewcubes.entities.GameMode
import de.pewpewproject.pewpewcubes.entities.state.PewPewCubesServerState
import java.nio.file.Files
import java.nio.file.Path

/**
 * Use case to initialize the server game mode state
 * @author Étienne Muser
 */
class InitializeServerGameModeUseCase(private val configFolderPath: Path) {
    operator fun invoke(serverId: String) {
        // Get the server state
        val state = PewPewCubesServerState.getInstance(serverId)

        // Get the path to the game mode file
        val gameModeFilePath = configFolderPath.resolve(LASERTAG_GAME_MODE_FILE_NAME)

        // Read the game mode file
        val gameModeTranslatableName = Files.readString(gameModeFilePath)

        // Get the game mode
        val gameMode = GameMode.entries.firstOrNull {
            it.translatableName == gameModeTranslatableName
        } ?: throw ConfigurationException("Game mode not found")

        // Set the game mode
        state.gameState.currentGameMode = gameMode
    }
}
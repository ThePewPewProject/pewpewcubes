package de.pewpewproject.pewpewcubes.state

import de.pewpewproject.pewpewcubes.entities.SettingsPreset
import java.util.*

class PewPewCubesServerState {
    companion object {
        @Volatile
        private var states: HashMap<String, PewPewCubesServerState> = HashMap()

        fun getInstance(serverId:String ): PewPewCubesServerState {
            if (states.containsKey(serverId)) {
                synchronized(this) {
                    if (states.containsKey(serverId)) {
                        states[serverId] = PewPewCubesServerState()
                    }
                }
            }
            return states[serverId]!!
        }
    }

    /**
     * Map mapping every settings preset name to the preset
     *      key:   The settings preset name
     *      value: The preset
     */
    val settingsPresets: Map<String, SettingsPreset> = HashMap()

    /**
     * Set of the uuids of all players that are permitted to start the game
     */
    val startGamePermittedPlayers: Set<UUID> = HashSet()

    /**
     * The game state
     */
    val gameState: PewPewCubesState = PewPewCubesState()
}
package de.pewpewproject.pewpewcubes.entities.state

/**
 * Singleton of the client state
 * @author Étienne Muser
 */
class PewPewCubesClientState private constructor() {
    companion object {
        @Volatile
        private var instance: PewPewCubesClientState? = null

        fun getInstance(): PewPewCubesClientState {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = PewPewCubesClientState()
                    }
                }
            }
            return instance!!
        }
    }

    /**
     * Flag to indicate this state has been initialized
     */
    val initialized: Boolean = false

    /**
     * The game state
     */
    val gameState: PewPewCubesState = PewPewCubesState()
}
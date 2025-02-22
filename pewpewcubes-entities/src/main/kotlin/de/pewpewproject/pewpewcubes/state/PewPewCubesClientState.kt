package de.pewpewproject.pewpewcubes.state

/**
 * Singleton of the client state
 * @author Étienne Muser
 */
class PewPewCubesClientState private constructor() {
    companion object {
        @Volatile
        private var instance: PewPewCubesState? = null

        fun getInstance(): PewPewCubesState {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = PewPewCubesState()
                    }
                }
            }
            return instance!!
        }
    }
}
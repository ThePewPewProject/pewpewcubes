package de.pewpewproject.pewpewcubes.entities

/**
 * TODO
 * @author Étienne Muser
 */
data class Team(
    val id: Int,
    val name: String,
    val color: Color,
    val spawnpointBlock: String?
) {
    companion object {
        /**
         * The static instance of the dummy team "Spectators"
         */
        val SPECTATORS = Team(0, "Spectators", Color(128u, 128u, 128u), null)
    }
}
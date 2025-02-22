package de.pewpewproject.pewpewcubes.core.ports

/**
 * Interface for accessing the mojang apij
 *
 * @author Étienne Muser
 */
interface IMojangApiAccess {

    /**
     * Get the url to the skin of a player given his username.
     *
     * @param playerName The username of the player
     * @return The url to the players name
     */
    fun getSkinUrl(playerName: String): String?;
}
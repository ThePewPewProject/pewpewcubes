package de.pewpewproject.pewpewcubes.mojangapiaccess.dtos

/**
 * Dto for the result coming from the mojang api for the user profile request
 *
 * @author Étienne Muser
 */
data class PlayerInfoDto(val name: String, val id: String)
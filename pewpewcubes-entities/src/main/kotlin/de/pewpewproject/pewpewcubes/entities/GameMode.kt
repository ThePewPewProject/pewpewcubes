package de.pewpewproject.pewpewcubes.entities

/**
 * Enum representing the available game modes
 * @author Étienne Muser
 */
enum class GameMode(val translatableName: String) {
    ELIMINATION("gameMode.elimination"),
    CAPTURE_THE_FLAG("game_mode.capture_the_flag"),
    MUSICAL_CHAIRS("gameMode.musical_chairs"),
    POINT_HUNTER("gameMode.point_hunter"),
}
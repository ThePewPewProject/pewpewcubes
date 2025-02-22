package de.pewpewproject.pewpewcubes.state

import de.pewpewproject.pewpewcubes.entities.GameMode
import de.pewpewproject.pewpewcubes.entities.Position
import de.pewpewproject.pewpewcubes.entities.Team
import de.pewpewproject.pewpewcubes.entities.settings.SettingDescription
import java.util.*

/**
 * The state of the PewPewCubes game
 * @author Étienne Muser
 */
class PewPewCubesState {
    /**
     * Map mapping every player's uuid to the overall score they achieved. This score gets never reset.
     *      key:   The players uuid
     *      value: The overall score the player achieved
     */
    val playerOverallScoreMap: Map<UUID, Long> = HashMap()

    /**
     * Map mapping every player's uuid to the score they are currently sitting on.
     *      key:   The players uuid
     *      value: The score the player is currently sitting on
     */
    val playerScoreMap: Map<UUID, Long> = HashMap()

    /**
     * Set of the uuids of all currently activated players
     */
    val activatedPlayerUuids: Set<UUID> = HashSet()

    /**
     * Map every lasertarget to the player uuids that have already hit it
     *      key:   The lasertargets block position
     *      value: Set containing the uuids of the players that have already hit the lasertarget
     */
    val alreadyHitByMap: Map<Position, Set<UUID>> = HashMap()

    /**
     * Map every lasertarget to their last hit time in world time
     *      key:   The lasertargets block position
     *      value: The lasertargets last hit time in world time
     */
    val lastHitTimeMap: Map<Position, Long> = HashMap()

    /**
     * The currently selected game mode
     */
    val currentGameMode: GameMode = GameMode.ELIMINATION

    /**
     * Map every player's uuid to their username. This is used to still show the username
     * of a player that got disconnected.
     *      key:    The players uuid
     *      value:  The players username
     */
    val playerUsernameCache: Map<UUID, String> = HashMap()

    /**
     * Map every teams id to the set of uuids of the players this team contains
     *      key:    The team id
     *      value:  Set of all player's uuids in the team
     */
    val teamMap: Map<Int, Set<UUID>> = HashMap()

    /**
     * Map every player's uuid to the team id of the team they are in
     *      key:    The players uuid
     *      value:  The team id of the team they are in
     */
    val playerTeamMap: Map<UUID, Int> = HashMap()

    /**
     * Map every game mode to the settings set in this game mode
     *      key:    The game mode of the settings
     *      value:  Map of every setting description to the value of the setting
     */
    val settings: Map<GameMode, Map<SettingDescription, Any>> = EnumMap(GameMode::class.java)

    /**
     * Set of all settings preset names
     */
    val settingsPresetNames: Set<String> = HashSet()

    /**
     * The time in seconds that has already elapsed in this game.
     */
    var gameTime: Long = 0

    /**
     * Flag to indicate whether a game is running or not.
     */
    var isGameRunning: Boolean = false

    /**
     * The progress of an action. Value is in range [0, 1] or -1.
     * If the value is -1, then no progress bar will be
     * displayed. Otherwise, a progressbar of this percentage
     * will be displayed.
     * Used in the in-game overlay.
     */
    var progress: Double = -1.0

    /**
     * An information string that will be displayed beside
     * the progress bar in the map loading screen. This
     * text is only shown if the mapLoadingProgress is not -1.
     */
    var mapLoadingStepString: String = ""

    /**
     * The progress of the current map loading step. Value is
     * in range [0, 1] or -1. If the value is -1, then not
     * progress bar will be displayed. Otherwise, a progressbar
     * of this percentage will be displayed.
     * Used in the map loading screen.
     */
    var mapLoadingProgress: Double = -1.0

    /**
     * The time in seconds that are left in the pre-game count
     * down. If the value is -1, then no count down will be
     * displayed. If the value is 0, then "GO" will be displayed
     * if the value is any other value, then this number will
     * be displayed.
     */
    var startingIn: Long = -1

    /**
     * The team id of the winning team in the last game.
     */
    var lastGameWinnerId: Int = -1

    /**
     * Maps every team's id to the number of flags they have left
     *      key:    The team id
     *      value:  The number of flags left in that team
     */
    val teamFlagMap: Map<Int, Long> = HashMap()

    /**
     * Maps every player to the number of flags he has captured. Only necessary on the server. Does not get
     * synced to the clients.
     *      key:    The players uuid
     *      value:  The number of flags the player has captured
     */
    val playerFlagCapturedMap: Map<UUID, Long> = HashMap()

    /**
     * Maps every player to the team id of the team he is currently holding the flag of. If a player is currently
     * not holding a flag, he has no entry in this map.
     *      key:    The players uuid
     *      value:  The team id of the team the player is currently holding the flag of
     */
    val playerHoldingFlagMap: Map<UUID, Int> = HashMap()

    /**
     * Map every player to the number of player he has eliminated
     *      key:    The players uuid
     *      value:  The number of players he eliminated
     */
    val playerEliminationCountMap: Map<UUID, Long> = HashMap()

    /**
     * Set of all players that have been eliminated
     */
    val eliminatedPlayersSet: Set<UUID> = HashSet()

    /**
     * Set of all teams that have been eliminated. Teams are saved by their ids.
     */
    val eliminatedTeamsSet: Set<Int> = HashSet()

    /**
     * Map mapping every eliminated team to their survive time
     *      key:    The teams id
     *      value:  The time in seconds the team got eliminated
     */
    val teamSurviveTimeMap: Map<Int, Long> = HashMap()

    /**
     * Map mapping every player's uuid to their survive time in seconds
     *      key:    The players uuid
     *      value:  The players survive time in seconds
     */
    val playerSurviveTimeMap: Map<UUID, Long> = HashMap()

    /**
     * The team config
     *     key:     The name of the team
     *     value:   The team object
     */
    val teamConfig: Map<String, Team> = HashMap()
}
package de.pewpewproject.lasertag.lasertaggame.state.management.server.synced;

import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;

import java.util.List;
import java.util.UUID;

/**
 * Interface for a server elimination manager
 *
 * @author Étienne Muser
 */
public interface IEliminationManager {

    /**
     * Eliminate a player
     *
     * @param eliminatedPlayerUuid The player who was eliminated
     * @param shooterUuid          The player who eliminated the player
     * @param playEliminationSound Flag to indicate whether the elimination sound should be played
     */
    void eliminatePlayer(UUID eliminatedPlayerUuid, UUID shooterUuid, boolean playEliminationSound);

    /**
     * Eliminate a team
     *
     * @param team The team to eliminate
     */
    void eliminateTeam(TeamDto team);

    /**
     * Check if a player is eliminated
     *
     * @param playerUuid The uuid of the player
     * @return True if the player is eliminated. Otherwise, false.
     */
    boolean isPlayerNotEliminated(UUID playerUuid);

    /**
     * Check if a team is not eliminated
     *
     * @param team The team to check
     * @return True if the team is still in the game. Otherwise, false.
     */
    boolean isTeamNotEliminated(TeamDto team);

    /**
     * Get the elimination count of a player
     *
     * @param playerUuid The uuid of the player
     * @return The elimination count of the player
     */
    long getPlayerEliminationCount(UUID playerUuid);

    /**
     * Get a list of all remaining team ids
     *
     * @return A list containing the team ids of all remaining teams
     */
    List<Integer> getRemainingTeamIds();

    /**
     * Reset the elimination manager
     */
    void reset();

    /**
     * Tick the elimination manager
     */
    void tick();

    /**
     * Get the survive time of a team
     *
     * @param team The team to get the survive time of
     * @return The survive time in seconds of the team
     */
    Long getTeamSurviveTime(TeamDto team);

    /**
     * Get a players survive time
     *
     * @param playerUuid The uuid of the player
     * @return The survive time in seconds of the player
     */
    Long getPlayerSurviveTime(UUID playerUuid);
}

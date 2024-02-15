package de.kleiner3.lasertag.lasertaggame.state.management.server.synced;

import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

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
     */
    void eliminatePlayer(UUID eliminatedPlayerUuid, UUID shooterUuid);

    /**
     * Check if a player is eliminated
     *
     * @param playerUuid The uuid of the player
     * @return True if the player is eliminated. Otherwise, false.
     */
    boolean isPlayerEliminated(UUID playerUuid);

    /**
     * Get the elimination count of a player
     *
     * @param playerUuid The uuid of the player
     * @return The elimination count of the player
     */
    long getPlayerEliminationCount(UUID playerUuid);

    /**
     * Reset the elimination manager
     */
    void reset();

    /**
     * Tick the elimination manager
     */
    void tick();

    /**
     * Set the survive time of a team
     *
     * @param team        The team to set the survive time of
     * @param surviveTime The survive time in seconds
     */
    void setTeamSurviveTime(TeamDto team, long surviveTime);

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

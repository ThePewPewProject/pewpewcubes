package de.kleiner3.lasertag.lasertaggame.state.server;

import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.UUID;

/**
 * Interface for a musical chairs state.
 * Resembles the state of a musical chairs game.
 *
 * @author Étienne Muser
 */
public interface IMusicalChairsState {

    /**
     * Set the survive time of a team
     *
     * @param team        The team
     * @param surviveTime The new survive time in seconds
     */
    void setTeamSurviveTime(TeamDto team, long surviveTime);

    /**
     * Get the survive time of a team
     *
     * @param teamDto The team
     * @return The survive time in seconds of the team
     */
    Long getTeamSurviveTime(TeamDto teamDto);

    /**
     * Set a players overall score
     *
     * @param playerUuid The players uuid
     * @param newScore   The new score of the player
     */
    void setPlayerOverallScore(UUID playerUuid, long newScore);

    /**
     * Get a players overall score
     *
     * @param playerUuid The uuid of the player
     * @return The players overall score
     */
    long getPlayerOverallScore(UUID playerUuid);

    /**
     * Resets the state to pre-game conditions
     */
    void reset();
}

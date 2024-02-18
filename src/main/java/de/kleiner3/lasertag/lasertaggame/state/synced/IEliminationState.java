package de.kleiner3.lasertag.lasertaggame.state.synced;

import java.util.UUID;

/**
 * Interface for an elimination state.
 * Resembles the state of what player eliminated how many other players and
 * what players are eliminated.
 *
 * @author Étienne Muser
 */
public interface IEliminationState {

    /**
     * Set the elimination count of a player
     *
     * @param playerUuid The uuid of the player
     * @param newCount   The new elimination count of the player
     */
    void setEliminationCount(UUID playerUuid, long newCount);

    /**
     * Get the elimination count of a player
     *
     * @param playerUuid The uuid of the player
     * @return The players elimination count
     */
    long getEliminationCount(UUID playerUuid);

    /**
     * Set a player to be eliminated
     *
     * @param playerUuid The uuid of the player
     */
    void eliminatePlayer(UUID playerUuid);

    /**
     * Check if a player is eliminated
     *
     * @param playerUuid The players uuid
     * @return True if the player is eliminated. Otherwise, false.
     */
    boolean isEliminated(UUID playerUuid);

    /**
     * Set a team to be eliminated
     *
     * @param teamId The teams id
     */
    void eliminateTeam(int teamId);

    /**
     * Check if a team is eliminated
     *
     * @param teamId The teams id
     * @return True if the team is eliminated. Otherwise, false.
     */
    boolean isEliminated(int teamId);

    /**
     * Set a teams survive time in seconds
     *
     * @param teamId      The id of the team
     * @param surviveTime The teams survive time in seconds
     */
    void setTeamSurviveTime(int teamId, long surviveTime);

    /**
     * Get a teams survive time in seconds
     *
     * @param teamId The id of the team
     * @return The teams survive time in seconds or null if the team survived to the end
     */
    Long getTeamSurviveTime(int teamId);

    /**
     * Set a players survive time in seconds
     *
     * @param playerUuid The uuid of the player
     * @param surviveTime The players survive time in seconds
     */
    void setPlayerSurviveTime(UUID playerUuid, long surviveTime);

    /**
     * Get a players survive time in seconds
     *
     * @param playerUuid The players uuid
     * @return The players surivive time in seconds or null if he survived to the end
     */
    Long getPlayerSuriviveTime(UUID playerUuid);

    /**
     * Reset the state to pre game conditions
     */
    void reset();
}

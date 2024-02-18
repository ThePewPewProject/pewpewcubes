package de.kleiner3.lasertag.lasertaggame.state.management.client;

import java.util.List;
import java.util.UUID;

/**
 * Interface for a client elimination manager
 *
 * @author Étienne Muser
 */
public interface IEliminationManager {

    /**
     * Get a players elimination count
     *
     * @param playerUuid The players uuid
     * @return The players elimination count
     */
    long getEliminationCount(UUID playerUuid);

    /**
     * Set a players elimination count
     *
     * @param playerUuid       The uuid of the player
     * @param eliminationCount The new elimination count of the player
     */
    void setEliminationCount(UUID playerUuid, long eliminationCount);

    /**
     * Get the ids of the remaining teams
     *
     * @return A list containing all team ids of the remaining teams
     */
    List<Integer> getRemainingTeamIds();

    /**
     * Set a team to be eliminated
     *
     * @param teamId The teams id
     */
    void setTeamEliminated(int teamId);

    /**
     * Set a teams survive time in seconds
     *
     * @param surviveTime The teams survive time
     */
    void setTeamSurviveTime(int teamId, long surviveTime);

    /**
     * Set a player to be eliminated
     *
     * @param playerUuid The players uuid
     */
    void setPlayerEliminated(UUID playerUuid);

    /**
     * Set a players survive time in seconds
     *
     * @param surviveTime The players survive time
     */
    void setPlayerSurviveTime(UUID playerUuid, long surviveTime);

    /**
     * Reset the elimination manager
     */
    void reset();
}

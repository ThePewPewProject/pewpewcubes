package de.kleiner3.lasertag.lasertaggame.state.management.client;

import java.util.List;

/**
 * Interface for a client remaining teams manager.
 *
 * @author Étienne Muser
 */
public interface IRemainingTeamsManager {

    /**
     * Remove a team from the remaining teams
     *
     * @param teamId The teams id
     */
    void removeTeam(int teamId);

    /**
     * Get a list of all remaining teams ids
     *
     * @return A list containing the ids of all remaining teams
     */
    List<Integer> getRemainingTeamIds();

    /**
     * Reset the remaining teams
     */
    void reset();
}

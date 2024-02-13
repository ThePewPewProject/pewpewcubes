package de.kleiner3.lasertag.lasertaggame.state.management.server.synced;

import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.List;

/**
 * Interface for a server remaining teams manager
 *
 * @author Étienne Muser
 */
public interface IRemainingTeamsManager {

    /**
     * Remove a team from the remaining teams
     *
     * @param team The team to remove
     */
    void removeTeam(TeamDto team);

    /**
     * Get the remaining teams
     *
     * @return A list containing all remaining teams
     */
    List<TeamDto> getRemainingTeams();

    /**
     * Get whether a team is still remaining
     *
     * @param team The team
     * @return True if the team is still remaining in the game. Otherwise, false.
     */
    boolean remains(TeamDto team);

    /**
     * Reset the remaining teams
     */
    void reset();
}

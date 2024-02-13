package de.kleiner3.lasertag.lasertaggame.state.synced;

import java.util.List;
import java.util.Set;

/**
 * Interface for a remaining teams state.
 * Resembles the state of what teams are remaining in the current game.
 *
 * @author Étienne Muser
 */
public interface IRemainingTeamsState {

    /**
     * Get a set of all remaining teams ids
     *
     * @return A set of all team ids of the remaining teams
     */
    Set<Integer> getRemainingTeams();

    /**
     * Remove a team from the remaining teams
     *
     * @param team The team to remove
     */
    void removeTeam(int team);

    /**
     * Get whether a team is remaining
     *
     * @param team The team to check
     * @return True if the team is still remaining in the game. Otherwise, false.
     */
    boolean remains(int team);

    /**
     * Reset the remaining teams to the provided list of teams
     *
     * @param teamConfig The now remaining teams
     */
    void reset(List<Integer> teamConfig);
}

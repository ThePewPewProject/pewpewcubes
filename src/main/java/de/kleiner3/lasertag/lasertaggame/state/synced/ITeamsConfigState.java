package de.kleiner3.lasertag.lasertaggame.state.synced;

import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.List;
import java.util.Optional;

/**
 * Interface for a teams config state.
 * Resembles the state of team configuration (What teams do exist).
 *
 * @author Étienne Muser
 */
public interface ITeamsConfigState {

    /**
     * Resets the state to the default state
     */
    void reset();

    /**
     * Reloads the team config state from the configuration file.
     */
    void reload();

    /**
     * Get a team by its id
     *
     * @param id The id of the team to get
     * @return The team of the corresponding id
     */
    Optional<TeamDto> getTeamOfId(int id);

    /**
     * Get a team by its name
     *
     * @param name The name of the team to get
     * @return The team of the corresponding name
     */
    Optional<TeamDto> getTeamOfName(String name);

    /**
     * Get all configured teams
     *
     * @return All teams
     */
    List<TeamDto> getTeams();

    /**
     * Set a new team config from a JSON string
     *
     * @param jsonString The new team config as a JSON string
     */
    void setTeamConfig(String jsonString);

    /**
     * Serialize the current team config to JSON
     *
     * @return A string containing the JSON representation of the current team config state
     */
    String toJson();
}

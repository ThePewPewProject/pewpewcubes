package de.kleiner3.lasertag.lasertaggame.state.synced;

import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Interface for a teams state.
 * Resembles the state of teams (What players are in what team).
 *
 * @author Étienne Muser
 */
public interface ITeamsState {

    /**
     * Set the player to be in a new team.
     *
     * @param playerUuid The uuid of the player
     * @param newTeamDto The new team of the player
     */
    void updateTeamOfPlayer(UUID playerUuid, TeamDto newTeamDto);

    /**
     * Remove a player from his team
     *
     * @param playerUuid The uuid of the player
     */
    void removePlayerFromTeam(UUID playerUuid);

    /**
     * Get all players of a team
     *
     * @param team The team to get the players of
     * @return A list of all players currently in that team
     */
    List<UUID> getPlayersOfTeam(TeamDto team);

    /**
     * Get the team of a player
     *
     * @param playerUuid The player to get the team of
     * @return The team of the player
     */
    Optional<Integer> getTeamOfPlayer(UUID playerUuid);

    /**
     * Execute a callback method on each team-id and player uuid for every player in a team
     *
     * @param callback The BiConsumer callback method to execute. Takes the team id and the player uuid.
     */
    void forEachPlayer(BiConsumer<Integer, UUID> callback);

    /**
     * Check if a player is currently in a team
     *
     * @param playerUuid The uuid of the player to check
     * @return True if the player is in a team. Otherwise, false.
     */
    boolean isPlayerInTeam(UUID playerUuid);

    /**
     * Resets the entire state to the configured teams. Every team will be emptied.
     *
     * @param teamsConfigState The team config state to reset to.
     */
    void reset(ITeamsConfigState teamsConfigState);

    /**
     * Serializes the state to JSON
     *
     * @return A JSON string of the state
     */
    String toJson();
}

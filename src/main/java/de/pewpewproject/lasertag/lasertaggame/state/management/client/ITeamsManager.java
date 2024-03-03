package de.pewpewproject.lasertag.lasertaggame.state.management.client;

import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface for a client teams manager.
 *
 * @author Étienne Muser
 */
public interface ITeamsManager {

    /**
     * Get the players of a team
     *
     * @param team The team to get the players of
     * @return A list containing all players of the team
     */
    List<UUID> getPlayersOfTeam(TeamDto team);

    /**
     * Update the team of a player
     *
     * @param playerUuid The uuid of the player to update the team of
     * @param newTeam    The new team of the player
     */
    void updateTeamOfPlayer(UUID playerUuid, TeamDto newTeam);

    /**
     * Remove a player from his team
     *
     * @param playerUuid The uuid of the player to remove from his team
     */
    void removePlayerFromTeam(UUID playerUuid);

    /**
     * Check if a player in a team
     *
     * @param playerUuid The uuid of the player to check
     * @return True if the player is in a team. Otherwise, false.
     */
    boolean isPlayerInTeam(UUID playerUuid);

    /**
     * Get the team of a player
     *
     * @param playerUuid The uuid of the player to get the team of
     * @return Optional containing the id of the team of the player if the player is in a team. Otherwise, Optional.empty.
     */
    Optional<Integer> getTeamOfPlayer(UUID playerUuid);
}

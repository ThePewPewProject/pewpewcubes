package de.pewpewproject.lasertag.lasertaggame.state.management.server.synced;

import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface for a server teams manager
 *
 * @author Étienne Muser
 */
public interface ITeamsManager {

    /**
     * Get all players of a team
     *
     * @param team The team to get the players of
     * @return List containing all players of the team
     */
    List<UUID> getPlayersOfTeam(TeamDto team);

    /**
     * Let a player join a team
     *
     * @param player     The player
     * @param newTeamDto The new team of the player
     * @return True if the player joined the team. False if the team is full.
     */
    boolean playerJoinTeam(PlayerEntity player, TeamDto newTeamDto);

    /**
     * Remove a player from his team
     *
     * @param player The player
     */
    void playerLeaveHisTeam(PlayerEntity player);

    /**
     * Remove a player from his team
     *
     * @param playerUuid The uuid of the player
     */
    void playerLeaveHisTeam(UUID playerUuid);

    /**
     * Check whether a player is in a team
     *
     * @param playerUuid The uuid of the player
     * @return True if the player is in a team. Otherwise, false.
     */
    boolean isPlayerInTeam(UUID playerUuid);

    /**
     * Get the team of a player
     *
     * @param playerUuid The uuid of the player
     * @return Optional containing the team of the player if the player is in a team. Otherwise, Optional.empty.
     */
    Optional<TeamDto> getTeamOfPlayer(UUID playerUuid);

    /**
     * Reload the team config from the config file
     */
    void reloadTeamsConfig();
}

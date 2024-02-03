package de.kleiner3.lasertag.lasertaggame.state.management.client;

import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface for a client capture the flag manager.
 *
 * @author Étienne Muser
 */
public interface ICaptureTheFlagManager {

    /**
     * Get the number of flags a team has left
     *
     * @param team The team to get the number of remaining flags of
     * @return The number of remaining flags of the team
     */
    long getNumberOfFlags(TeamDto team);

    /**
     * Get the team of the flag a player is holding
     *
     * @param playerUuid The uuid of the player
     * @return Optional containing the team of the flag the player is holding if he is holding a flag. Otherwise, Optional.empty.
     */
    Optional<TeamDto> getPlayerHoldingFlagTeam(UUID playerUuid);

    /**
     * Update the flag a player is holding
     *
     * @param playerUuid The uuid of the player
     * @param teamId     The team id of the team of the flag
     */
    void updateFlagHolding(UUID playerUuid, int teamId);

    /**
     * Let the player drop his flag
     *
     * @param playerUuid The uuid of the player
     */
    void removeFlagHolding(UUID playerUuid);

    /**
     * Reset the state to pre-game conditions
     */
    void reset();

    /**
     * Update the flag count of a team
     *
     * @param team             The team to update the flag count of
     * @param newNumberOfFlags The new number of flags of the team
     */
    void updateTeamFlagCount(TeamDto team, long newNumberOfFlags);
}

package de.kleiner3.lasertag.lasertaggame.state.synced;

import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface for a capture the flag state.
 * Resembles the state of a capture the flag game (What team has how many flags; What player holds what flag; etc).
 *
 * @author Étienne Muser
 */
public interface ICaptureTheFlagState {

    /**
     * Resets the state to pre-game conditions
     */
    void reset();

    /**
     * Take away the flag of a player
     *
     * @param playerUuid The uuid of the player to take the flag from
     */
    void playerDropFlag(UUID playerUuid);

    /**
     * Give a flag to a player
     *
     * @param playerUuid The uuid of the player to give the flag to
     * @param team       The id of the team of the flag
     */
    void playerPickupFlag(UUID playerUuid, int team);

    /**
     * Get the team id of the team whose flag the player is holding
     *
     * @param playerUuid The uuid of the player
     * @return Optional containing the id of the team of the flag the player is holding. Or Optional.empty if the player
     * isn't holding a flag
     */
    Optional<Integer> getPlayerHoldingFlagTeam(UUID playerUuid);

    /**
     * Get the number of flags a player has captured
     *
     * @param playerUuid The uuid of the player
     * @return The number of flags the player has captured
     */
    long getNumberOfCapturedFlags(UUID playerUuid);

    /**
     * Set the number of flags a player has captured
     *
     * @param playerUuid           The uuid of the player
     * @param newFlagCapturedCount The new amount of captured flags
     */
    void setNumberOfCapturedFlags(UUID playerUuid, long newFlagCapturedCount);

    /**
     * Get how long a team survived in seconds
     *
     * @param team The team to get the survival duration from
     * @return Optional containing the survival duration. Or Optional.empty if the team survived till the end
     */
    Optional<Long> getSurviveTime(TeamDto team);

    /**
     * Set how long a team survived in seconds
     *
     * @param team        The team to update the survival duration of
     * @param surviveTime The new survival duration
     */
    void setSurviveTime(TeamDto team, long surviveTime);

    /**
     * Update the flag count of a team
     *
     * @param team         The team to update the flag count of
     * @param newFlagCount The new flag count
     */
    void updateTeamFlagCount(TeamDto team, long newFlagCount);

    /**
     * Get how many flags a team has left
     *
     * @param team The team to get the flag count of
     * @return The flag count
     */
    long getNumberOfFlags(TeamDto team);
}

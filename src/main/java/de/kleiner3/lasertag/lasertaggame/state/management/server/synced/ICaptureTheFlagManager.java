package de.kleiner3.lasertag.lasertaggame.state.management.server.synced;

import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface for a server capture the flag manager
 *
 * @author Étienne Muser
 */
public interface ICaptureTheFlagManager {

    /**
     * Reset to pre-game conditions
     */
    void reset();

    /**
     * Make a player drop his flag
     *
     * @param playerUuid The uuid of the player
     */
    void playerDropFlag(UUID playerUuid);

    /**
     * Make a player pick up a flag
     *
     * @param player The player
     * @param team   The team of the flag
     */
    void playerPickupFlag(ServerPlayerEntity player, TeamDto team);

    /**
     * Get the team of the flag a player is holding
     *
     * @param playerUuid The uuid of the player
     * @return Optional containing the team of the flag if the player is holding a flag. Otherwise, Optional.empty.
     */
    Optional<TeamDto> getPlayerHoldingFlagTeam(UUID playerUuid);

    /**
     * Get the number of flags a player has captured
     *
     * @param playerUuid The uuid of the player
     * @return The number of flags the player captured
     */
    long getNumberOfCapturedFlags(UUID playerUuid);


    /**
     * Get the number of flags a team has left
     *
     * @param teamDto The team
     * @return The number of flags the team has left
     */
    long getNumberOfFlags(TeamDto teamDto);

    /**
     * A flag of a team got captured
     *
     * @param playerUuid The uuid of the player who captured the flag
     * @param team       The team whose flag got captured
     */
    void flagCaptured(UUID playerUuid, TeamDto team);
}

package de.kleiner3.lasertag.lasertaggame.state.management.server;

import de.kleiner3.lasertag.lasertaggame.team.TeamDto;

import java.util.UUID;

/**
 * Interface for a server musical chairs manager
 *
 * @author Étienne Muser
 */
public interface IMusicalChairsManager {

    /**
     * Tick the manager.
     */
    void tick();

    /**
     * Get a teams survive time
     *
     * @param team The team
     * @return The teams survive time in seconds
     */
    Long getTeamSurvivedTime(TeamDto team);

    /**
     * Get a players total score
     *
     * @param playerUuid The players uuid
     * @return The players total score
     */
    long getPlayerTotalScore(UUID playerUuid);

    /**
     * Increase a players total score by the given amount
     *
     * @param playerUuid The players uuid
     * @param score      The players score to add
     */
    void onPlayerScored(UUID playerUuid, long score);

    /**
     * Reset the ticks since last phase
     */
    void resetTicksSinceLastPhase();
}

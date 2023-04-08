package de.kleiner3.lasertag.lasertaggame;

import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Interface providing methods for a lasertag player
 *
 * @author Étienne Muser
 */
public interface ILasertagPlayer {
    /**
     * Called when this player got hit by another player
     *
     * @param player The player who hit this player
     */
    default void onHitBy(PlayerEntity player) {
        // Default empty
    }

    /**
     * Set the players team
     * @param teamDto The  team
     */
    default void setTeam(TeamDto teamDto) {
        // Default empty
    }

    /**
     * Get the players team
     * @return The players team
     */
    default TeamDto getTeam() {
        return null;
    }

    /**
     * Called when the player is deactivated
     */
    default void onDeactivated() {
        // Default empty
    }

    /**
     * Called when the player gets activated
     */
    default void onActivated() {
        // Default empty
    }

    default String getLasertagUsername() {
        return null;
    }
}

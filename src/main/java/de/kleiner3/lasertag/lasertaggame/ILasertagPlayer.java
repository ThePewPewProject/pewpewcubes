package de.kleiner3.lasertag.lasertaggame;

import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamDto;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Interface providing methods for a lasertag player
 *
 * @author Étienne Muser
 */
public interface ILasertagPlayer {
    /**
     * @return The lasertag score of the player
     */
    default int getLasertagScore() {
        return -1;
    }

    /**
     * Reset the players lasertag score to 0
     */
    default void resetLasertagScore() {
    }

    /**
     * Increase the players score by the given amount
     *
     * @param score The amount
     */
    default void increaseScore(int score) {
    }

    /**
     * Called when this player got hit by another player
     *
     * @param player The player who hit this player
     */
    default void onHitBy(PlayerEntity player) {
    }

    /**
     * Set the players team
     * @param teamDto The  team
     */
    default void setTeam(TeamDto teamDto) {
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
    }

    /**
     * Called when the player gets activated
     */
    default void onActivated() {
    }

    default String getLasertagUsername() {
        return null;
    }
}

package de.kleiner3.lasertag.lasertaggame;

import de.kleiner3.lasertag.types.Colors;
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
     * @param team The color of the team
     */
    default void setTeam(Colors.Color team) {
    }

    /**
     * Get the players team
     * @return The color of the players team
     */
    default Colors.Color getTeam() {
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

package de.kleiner3.lasertag.lasertaggame;

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
    default public int getLasertagScore() {
        return -1;
    }

    /**
     * Reset the players lasertag score to 0
     */
    default public void resetLasertagScore() {
    }

    /**
     * Increase the players score by the given amount
     *
     * @param score
     */
    default public void increaseScore(int score) {
    }

    /**
     * Called when this player got hit by another player
     *
     * @param player
     */
    default public void onHitBy(PlayerEntity player) {
    }

    default public void onDeactivated() {}

    default public void onActivated() {}
}

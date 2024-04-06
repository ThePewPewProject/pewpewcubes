package de.pewpewproject.lasertag.lasertaggame.state.management.server;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Interface for a start game permission manager
 *
 * @author Étienne Muser
 */
public interface IStartGamePermissionManager {

    /**
     * Check if a player is permitted to start a game
     *
     * @param player The player
     * @return True if the player is permitted to start a game. Otherwise, false.
     */
    boolean isStartGamePermitted(PlayerEntity player);

    /**
     * Set a player permitted to start a game
     *
     * @param player The player
     */
    void setStartGamePermitted(PlayerEntity player);

    /**
     * Take the permission to start a game from a player
     *
     * @param player The player
     */
    void setNotStartGamePermitted(PlayerEntity player);
}

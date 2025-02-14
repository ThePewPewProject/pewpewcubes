package de.pewpewproject.lasertag.client.screen;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Interface to open the lasertag credits screen. Gets injected into the client player entity
 *
 * @author Étienne Muser
 */
public interface ILasertagCreditsScreenOpener {
    /**
     * Open the lasertag credits screen on this client
     *
     * @param player The player that opened the screen
     */
    default void openLasertagCreditsScreen(PlayerEntity player){
        // Default empty
    }
}

package de.pewpewproject.lasertag.client.screen;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Interface to open the lasertag game manager screen. Gets injected into the client player entity
 *
 * @author Étienne Muser
 */
public interface ILasertagGameManagerScreenOpener {

    /**
     * Open the lasertag game manager gui on this client
     *
     * @param player The player that opened the screen
     */
    default void openLasertagGameManagerScreen(PlayerEntity player){
        // Default empty
    }
}

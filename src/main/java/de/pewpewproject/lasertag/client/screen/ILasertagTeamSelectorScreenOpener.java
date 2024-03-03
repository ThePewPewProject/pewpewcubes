package de.pewpewproject.lasertag.client.screen;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Interface to open the lasertag team selector screen. Gets injected into the client player entity
 *
 * @author Étienne Muser
 */
public interface ILasertagTeamSelectorScreenOpener {
    /**
     * Open the lasertag team selector gui on this client
     */
    default void openLasertagTeamSelectorScreen(PlayerEntity player) {
        // Default empty
    }
}

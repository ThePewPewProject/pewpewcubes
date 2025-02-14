package de.pewpewproject.lasertag.client.screen;

import de.pewpewproject.lasertag.block.entity.LasertagTeamZoneGeneratorBlockEntity;

/**
 * Interface to open the lasertag team zone generator screen. Gets injected into the client player entity
 *
 * @author Étienne Muser
 */
public interface ILasertagTeamZoneGeneratorScreenOpener {
    /**
     * Open the lasertag team selector gui on this client
     */
    default void openLasertagTeamZoneGeneratorScreen(LasertagTeamZoneGeneratorBlockEntity teamZoneGenerator) {
        // Default empty
    }
}

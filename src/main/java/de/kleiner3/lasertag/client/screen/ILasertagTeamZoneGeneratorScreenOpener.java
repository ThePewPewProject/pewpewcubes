package de.kleiner3.lasertag.client.screen;

import de.kleiner3.lasertag.block.entity.LasertagTeamZoneGeneratorBlockEntity;

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

package de.kleiner3.lasertag.client.screen;

/**
 * Interface to open the lasertag team selector screen. Gets injected into the client player entity
 *
 * @author Étienne Muser
 */
public interface ILasertagTeamSelectorScreenOpener {
    /**
     * Open the lasertag team selector gui on this client
     */
    default void openLasertagTeamSelectorScreen() {
        // Default empty
    }
}

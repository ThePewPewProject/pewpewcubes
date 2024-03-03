package de.pewpewproject.lasertag.lasertaggame.state.management.client;

/**
 * Accessor interface for the client lasertag manager. Gets injected into the ClientWorld.class.
 *
 * @author Étienne Muser
 */
public interface IClientLasertagManagerAccessor {

    default IClientLasertagManager getClientLasertagManager() { return null; }
}

package de.pewpewproject.lasertag.lasertaggame.state.management.server;

/**
 * Server lasertag manager accessor. Gets injected into the ServerWorld.class
 *
 * @author Étienne Muser
 */
public interface IServerLasertagManagerAccessor {

    default IServerLasertagManager getServerLasertagManager() { return null; }
}

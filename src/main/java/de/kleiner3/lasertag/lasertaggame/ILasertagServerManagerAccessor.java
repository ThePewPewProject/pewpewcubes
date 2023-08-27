package de.kleiner3.lasertag.lasertaggame;

import de.kleiner3.lasertag.lasertaggame.management.LasertagServerManager;

/**
 * Injects a getter for the LasertagServerManager into the MinecraftServer
 *
 * @author Étienne Muser
 */
public interface ILasertagServerManagerAccessor {

    default LasertagServerManager getLasertagServerManager() {
        return null;
    }
}

package de.kleiner3.lasertag.lasertaggame.state.management.server;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;

/**
 * Interface for a server lasertarget manager
 *
 * @author Étienne Muser
 */
public interface ILasertargetManager {

    /**
     * Register a lasertarget for resetting
     *
     * @param target The lasertarget to register
     */
    void registerLasertarget(LaserTargetBlockEntity target);

    /**
     * Reset all registered lasertargets
     */
    void resetLasertargets();
}

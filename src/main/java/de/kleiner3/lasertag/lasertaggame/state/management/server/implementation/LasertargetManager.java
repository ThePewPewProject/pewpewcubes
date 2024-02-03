package de.kleiner3.lasertag.lasertaggame.state.management.server.implementation;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.lasertaggame.state.management.server.ILasertargetManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of ILasertargetManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class LasertargetManager implements ILasertargetManager {

    private final Set<LaserTargetBlockEntity> lasertargetsToReset = new HashSet<>();

    /**
     * Register a lasertarget to be reset after game
     * @param target The lasertarget block entity to register
     */
    public synchronized void registerLasertarget(LaserTargetBlockEntity target) {

        lasertargetsToReset.add(target);
    }

    /**
     * Resets all registered lasertargets
     */
    public synchronized void resetLasertargets() {

        for (var lasertarget : lasertargetsToReset) {
            lasertarget.reset();
        }
        lasertargetsToReset.clear();
    }
}

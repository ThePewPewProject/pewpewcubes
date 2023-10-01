package de.kleiner3.lasertag.lasertaggame.management.lasertargets;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.lasertaggame.management.IManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to manage the lasertargets
 *
 * @author Étienne Muser
 */
public class LasertargetManager implements IManager {

    private Set<LaserTargetBlockEntity> lasertargetsToReset = new HashSet<>();

    /**
     * Register a lasertarget to be reset after game
     * @param target The lasertarget block entity to register
     */
    public void registerLasertarget(LaserTargetBlockEntity target) {

        lasertargetsToReset.add(target);
    }

    /**
     * Resets all registered lasertargets
     */
    public void resetLasertargets() {

        for (var lasertarget : lasertargetsToReset) {
            lasertarget.reset();
        }
        lasertargetsToReset = new HashSet<>();
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }
}

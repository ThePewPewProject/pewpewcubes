package de.kleiner3.lasertag.lasertaggame.state.synced.implementation;

import de.kleiner3.lasertag.lasertaggame.state.synced.ILasertargetState;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of ILasertargetState for the lasertag game
 *
 * @author Étienne Muser
 */
public class LasertargetState implements ILasertargetState {

    /**
     * Map every lasertarget to the player uuids that have already hit it
     *     key:   The lasertargets block position
     *     value: Set containing the uuids of the players that have already hit the lasertarget
     */
    private final Map<BlockPos, Set<UUID>> alreadyHitByMap = new ConcurrentHashMap<>();

    /**
     * Map every lasertarget to their last hit time
     *     key:   The lasertargets block position
     *     value: The lasertargets last hit time
     */
    private final Map<BlockPos, Long> lastHitTimeMap = new ConcurrentHashMap<>();

    /**
     * Set containing all currently deactivated lasertargets block positions
     */
    private final Set<BlockPos> deactivatedLasertargets = new HashSet<>();

    @Override
    public synchronized boolean isAlreadyHitBy(BlockPos lasertargetPos, UUID playerUuid) {

        // If the lasertarget has no entry in the already hit by map
        if (!alreadyHitByMap.containsKey(lasertargetPos)) {

            // Create entry
            alreadyHitByMap.put(lasertargetPos, new HashSet<>());
        }

        // Return true if the already hit by map entry of the lasertarget contains the players uuid
        return alreadyHitByMap.get(lasertargetPos).contains(playerUuid);
    }

    @Override
    public synchronized void setHitBy(BlockPos lasertargetPos, UUID playerUuid) {

        // If the lasertarget has no entry in the already hit by map
        if (!alreadyHitByMap.containsKey(lasertargetPos)) {

            // Create entry
            alreadyHitByMap.put(lasertargetPos, new HashSet<>());
        }

        // Add the player to the lasertargets map entry
        alreadyHitByMap.get(lasertargetPos).add(playerUuid);
    }

    @Override
    public void setLastHitTime(BlockPos lasertargetPos, long hitTime) {

        lastHitTimeMap.put(lasertargetPos, hitTime);
    }

    @Override
    public long getLastHitTime(BlockPos lasertargetPos) {
        return Optional.ofNullable(lastHitTimeMap.get(lasertargetPos)).orElse(0L);
    }

    @Override
    public synchronized void setDeactivated(BlockPos lasertargetPos) {
        deactivatedLasertargets.add(lasertargetPos);
    }

    @Override
    public synchronized void setActivated(BlockPos lasertargetPos) {
        deactivatedLasertargets.remove(lasertargetPos);
    }

    @Override
    public synchronized boolean isDeactivated(BlockPos lasertargetPos) {
        return deactivatedLasertargets.contains(lasertargetPos);
    }

    @Override
    public synchronized void resetAlreadyHitBy() {

        alreadyHitByMap.clear();
    }

    @Override
    public void reset() {

        alreadyHitByMap.clear();
        deactivatedLasertargets.clear();
        alreadyHitByMap.clear();
    }
}

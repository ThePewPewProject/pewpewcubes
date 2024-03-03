package de.pewpewproject.lasertag.lasertaggame.state.management.server.synced;

import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Interface for a lasertargets manager
 *
 * @author Étienne Muser
 */
public interface ILasertargetsManager {

    /**
     * Set the last hit time of a lasertarget
     *
     * @param lasertargetPos The lasertargets block position
     * @param hitTime        The lasertargets new last hit time
     */
    void setLastHitTime(BlockPos lasertargetPos, long hitTime);

    /**
     * Deactivates the lasertarget and reactivates it after the configured amount of time
     *
     * @param lasertargetPos The block position of the lasertarget
     */
    void deactivate(BlockPos lasertargetPos);

    /**
     * Get if a lasertarget is deactivated
     *
     * @param lasertargetPos The lasertargets block position
     * @return True if the lasertarget is deactivated. Otherwise, false
     */
    boolean isDeactivated(BlockPos lasertargetPos);

    /**
     * Set a lasertarget to be hit by a player
     *
     * @param lasertargetPos The lasertargets block position
     * @param playerUuid     The players uuid
     */
    void setHitBy(BlockPos lasertargetPos, UUID playerUuid);

    /**
     * Get if a lasertarget has already been hit by a player
     *
     * @param lasertargetPos The lasertargets block position
     * @param playerUuid The players uuid
     * @return True if the player already hit the lasertarget. Otherwise, false.
     */
    boolean isAlreadyHitBy(BlockPos lasertargetPos, UUID playerUuid);

    /**
     * Reset the already hit by state of all lasertargets
     */
    void resetAlreadyHitBy();

    /**
     * Reset the entire lasertarget state
     */
    void reset();
}

package de.kleiner3.lasertag.lasertaggame.state.management.client;

import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Interface for a client lasertargets manager.
 *
 * @author Étienne Muser
 */
public interface ILasertargetsManager {

    /**
     * Set the last hit time of a lasertarget
     *
     * @param lasertargetPos The lasertargets block position
     * @param hitTime        The new hit time
     */
    void setLastHitTime(BlockPos lasertargetPos, long hitTime);

    /**
     * Get the last hit time of a lasertarget
     *
     * @param lasertargetPos The lasertargets block position
     * @return The last hit time of the lasertarget
     */
    long getLastHitTime(BlockPos lasertargetPos);

    /**
     * Set a lasertargets activation state
     *
     * @param lasertargetPos The lasertargets block position
     * @param isDeactivated  Flag to indicate if the lasertarget is deactivated or not
     */
    void setDeactivated(BlockPos lasertargetPos, boolean isDeactivated);

    /**
     * Get if a lasertarget is deactivated
     *
     * @param lasertargetPos The lasertargets block position
     * @return True if the lasertarget is deactivated. Otherwise, false.
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
     * Reset the entire lasertargets state
     */
    void reset();

    /**
     * Reset all lasertargets already hit by state
     */
    void resetAlreadyHitBy();
}

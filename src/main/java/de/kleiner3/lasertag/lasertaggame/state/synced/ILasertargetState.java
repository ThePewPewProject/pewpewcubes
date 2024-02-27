package de.kleiner3.lasertag.lasertaggame.state.synced;

import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Interface for a lasertarget state.
 * Resembles the state of all lasertargets. Which are deactivated, which are already hit by which player, what is the
 * last hit time on the lasertargets.
 *
 * @author Étienne Muser
 */
public interface ILasertargetState {

    /**
     * Check if a lasertarget has already been hit by another player
     *
     * @param lasertargetPos The block position of the lasertarget
     * @param playerUuid     The uuid of the player
     * @return
     */
    boolean isAlreadyHitBy(BlockPos lasertargetPos, UUID playerUuid);

    /**
     * Set a lasertarget to be hit by a player
     *
     * @param lasertargetPos The block position of the lasertarget
     * @param playerUuid     The uuid of the player
     */
    void setHitBy(BlockPos lasertargetPos, UUID playerUuid);

    /**
     * Set the last hit time of a lasertarget
     *
     * @param lasertargetPos The block position of the lasertarget
     * @param hitTime        The new hit time
     */
    void setLastHitTime(BlockPos lasertargetPos, long hitTime);

    /**
     * Get the last hit time of a lasertarget
     *
     * @param lasertargetPos The block position of the lasertarget
     * @return The lasertargets last hit time
     */
    long getLastHitTime(BlockPos lasertargetPos);

    /**
     * Set a lasertarget to be deactivated
     *
     * @param lasertargetPos The block position of the lasertarget
     */
    void setDeactivated(BlockPos lasertargetPos);

    /**
     * Set a lasertarget to be activated
     *
     * @param lasertargetPos The block position of the lasertarget
     */
    void setActivated(BlockPos lasertargetPos);

    /**
     * Get if a lasertarget is activated
     *
     * @param lasertargetPos The block position of the lasertarget
     * @return True if the lasertarget is activated. Otherwise, false.
     */
    boolean isDeactivated(BlockPos lasertargetPos);

    /**
     * Reset by which players the lasertargets have already been hit by
     */
    void resetAlreadyHitBy();

    /**
     * Resets the lasertarget state
     */
    void reset();
}

package de.pewpewproject.lasertag.common.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * TODO
 *
 * @author Étienne Muser
 */
public interface IFastChunk {

    /**
     * TODO
     *
     * @param pos
     * @param state
     * @param moved
     * @return
     */
    default BlockState fastSetBlockState(BlockPos pos, BlockState state, boolean moved) {
        return null;
    }
}

package de.kleiner3.lasertag.util.fastiter;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

/**
 * Interface providing the method which is being iterated over every block in the fast block iteration
 *
 * @author Étienne Muser
 */
public interface IIter {
    /**
     * The method called on every block in the fast block iteration
     * @param block The current block
     * @param pos The BlockPos of the current block
     */
    void doIter(Block block, BlockPos pos);
}

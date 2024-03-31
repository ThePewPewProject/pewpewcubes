package de.pewpewproject.lasertag.common.util;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Interface providing the fast block iteration method
 *
 * @author Étienne Muser
 */
public interface IFastWorld {
    /**
     * Iterates over every block in a predefined square of chunks.
     * Begins with 0, 0 and spirals outwards.
     * This method is highly optimized to iterate over every block as fast as possible.
     * <p>
     * Uses a Threadpool with the fixed size of the number of available processors in the system.
     *
     * @param iter     The method called on every block. MUST BE THREAD-SAFE! This method can be called on multiple threads at the same time.
     * @param progress The method called on every chunk after iteration over every block in this chunk has finished. MUST BE THREAD-SAFE! This method can be called on multiple threads at the same time.
     *                 params: first: the current chunk index, second: the max number of chunks
     */
    default void fastSearchBlock(BiConsumer<Block, BlockPos> iter, BiConsumer<Integer, Integer> progress) {
        // Default empty
    }

    /**
     * Iterates over every chunk in the given bounds
     *
     * @param chunkConsumer The action, which is performed on every chunk. MUST BE THREAD-SAFE! This method can be called on multiple threads at the same time.
     * @param progress Progress callback method. MUST BE THREAD-SAFE! This method can be called on multiple threads at the same time.
     *                 params: first: the current chunk index, second: the max number of chunks
     * @param startX The x-pos of the first chunk
     * @param endX The x-pos of the last chunk
     * @param startZ The z-pos of the first chunk
     * @param endZ The z-pos of the last chunk
     */
    default void fastChunkIter(Consumer<WorldChunk> chunkConsumer,
                                          BiConsumer<Integer, Integer> progress,
                                          int startX,
                                          int endX,
                                          int startZ,
                                          int endZ) {
        // Default empty
    }

    /**
     * TODO
     *
     * @param chunk
     * @param blockPos
     * @param blockState
     */
    default boolean fastSetBlock(WorldChunk chunk, BlockPos blockPos, BlockState blockState, int flags) {
        // Default empty
        return false;
    }
}

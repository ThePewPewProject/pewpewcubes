package de.kleiner3.lasertag.mixin.fastiter;

import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.common.util.fastiter.IFastWorldIter;
import de.kleiner3.lasertag.common.util.fastiter.IIter;
import de.kleiner3.lasertag.common.util.fastiter.IProgressReport;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Mixin into World to provide the fast block iteration method
 *
 * @author Étienne Muser
 */
@Mixin(World.class)
public class WorldMixin implements IFastWorldIter {
    /**
     * The length in chunks of a side of the square which is being searched.
     */
    private static final int EDGE_LENGTH = 31;

    /**
     * The number of ChunkSections in a chunk
     */
    private static final int NUM_SECTIONS = 24;

    @Override
    public void fastSearchBlock(IIter iter, IProgressReport progress) {
        // Spiral iteration: https://stackoverflow.com/a/14010215/20247322

        // Create the fixed size threadpool
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Calculate the number of chunks being iterated over
        final int numChunks = EDGE_LENGTH * EDGE_LENGTH;

        // Spiral iteration variables
        int layer = 1;
        int leg = 0;
        int x = 0;
        int z = 0;

        // For every chunk
        for (int currChunkIdx = 0; currChunkIdx < numChunks; ++currChunkIdx) {
            final int c = currChunkIdx;

            // Get the start block coordinates
            final int chunkBaseX = x * 16;
            final int chunkBaseZ = z * 16;

            // Get the chunk
            final var chunk = ((World) (Object) this).getChunk(x, z);

            // Continue on new thread
            executor.submit(() -> {
                // Get the chunks sections
                final var sections = chunk.getSectionArray();

                // For every section
                for (int secIdx = 0; secIdx < NUM_SECTIONS; secIdx++) {
                    // Get the section
                    final var section = sections[secIdx];
                    final int secBaseY = (secIdx - 4) * 16;

                    // For every block in the section
                    for (int sx = 0; sx < 16; sx++) {
                        for (int sy = 0; sy < 16; sy++) {
                            for (int sz = 0; sz < 16; sz++) {
                                // Get the block state
                                final var state = section.getBlockState(sx, sy, sz);

                                // If block is air, skip
                                if (state.equals(Blocks.AIR.getDefaultState())) {
                                    continue;
                                }

                                // If block is stone, skip
                                if (state.equals(Blocks.STONE.getDefaultState())) {
                                    continue;
                                }

                                // Call the iter method
                                iter.doIter(state.getBlock(), new BlockPos(chunkBaseX + sx, secBaseY + sy, chunkBaseZ + sz));
                            }
                        }
                    }
                }

                // Call the progress method
                progress.onProgress(c, numChunks);
            });

            // Go next in spiral
            switch (leg) {
                case 0 -> {
                    ++x;
                    if (x == layer) ++leg;
                }
                case 1 -> {
                    ++z;
                    if (z == layer) ++leg;
                }
                case 2 -> {
                    --x;
                    if (-x == layer) ++leg;
                }
                case 3 -> {
                    --z;
                    if (-z == layer) {
                        leg = 0;
                        ++layer;
                    }
                }
                default -> { /* Do nothing */ }
            }
        }

        // Wait for threadpool to finish
        ThreadUtil.attemptShutdown(executor, 30L);
    }

    // 40 x 40 Chunks single threaded -   ~20sec   (with sysout)
    // 40 x 40 Chunks multi threaded -    ~0.65sec (with sysout)
    // 64 x 64 Chunks multi threaded -    ~0.85sec (with sysout, after loading all chunks, 32 Chunk Render distance)
    // 64 x 64 Chunks thread pool -       ~0.76sec (with sysout, after loading all chunks, 32 Chunk Render distance)
    // 64 x 64 Chunks thread pool -       ~2.7sec  (with sysout, without explicitly loading the chunks, 32 Chunk Render distance)
    // 63 x 63 Chunks thread pool, spiral ~2sec    (with sysout, without explicitly loading the chunks, 32 Chunk Render distance)
    // 63 x 63 Chunks thread pool, spiral ~7.16sec (with sysout, without explicitly loading the chunks, 12 Chunk Render distance)
}

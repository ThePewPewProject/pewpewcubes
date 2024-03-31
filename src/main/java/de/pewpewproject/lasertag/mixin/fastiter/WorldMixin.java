package de.pewpewproject.lasertag.mixin.fastiter;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.common.util.ThreadUtil;
import de.pewpewproject.lasertag.common.util.IFastWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Mixin into World to provide the fast block iteration method
 *
 * @author Étienne Muser
 */
@Mixin(World.class)
public class WorldMixin implements IFastWorld {
    /**
     * The length in chunks of a side of the square which is being searched.
     */
    private static final int EDGE_LENGTH = 31;

    /**
     * The number of ChunkSections in a chunk
     */
    private static final int NUM_SECTIONS = 24;

    @Override
    public void fastSearchBlock(BiConsumer<Block, BlockPos> iter, BiConsumer<Integer, Integer> progress) {
        // Spiral iteration: https://stackoverflow.com/a/14010215/20247322

        // Create the fixed size threadpool
        var executor = ThreadUtil.createThreadPool("fast-search-block-%d");

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
                                iter.accept(state.getBlock(), new BlockPos(chunkBaseX + sx, secBaseY + sy, chunkBaseZ + sz));
                            }
                        }
                    }
                }

                // Call the progress method
                progress.accept(c, numChunks);
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


    @Override
    public void fastChunkIter(Consumer<WorldChunk> chunkConsumer,
                                         BiConsumer<Integer, Integer> progress,
                                         int startX,
                                         int endX,
                                         int startZ,
                                         int endZ) {

        // Calculate the number of chunks being iterated over
        final int numChunks = (endX - startX) * (endZ - startZ);

        // Create the fixed size threadpool
        var executor = ThreadUtil.createThreadPool("fast-chunk-iter-%d");

        // Create a counter to count what chunk we are currently at
        var currChunkIndex = new AtomicInteger(0);

        // For every slice of chunks in z-direction
        for (var chunkZ = startZ; chunkZ <= endZ; ++chunkZ) {

            // For every chunk in the slice
            for (var chunkX = startX; chunkX <= endX; ++chunkX) {

                // Get the chunk
                final var chunk = ((World) (Object) this).getChunk(chunkX, chunkZ);

                // Execute the action
                executor.submit(() -> {
                    chunkConsumer.accept(chunk);

                    // Call the progress method
                    progress.accept(currChunkIndex.getAndIncrement(), numChunks);
                });
            }
        }

        // Wait for threadpool to finish
        ThreadUtil.attemptShutdown(executor, 180L);
    }

    @Override
    public boolean fastSetBlock(WorldChunk worldChunk, BlockPos pos, BlockState state, int flags) {

        var that = (World)(Object)this;

        if (that.isOutOfHeightLimit(pos)) {
            return false;
        } else if (!that.isClient && that.isDebugWorld()) {
            return false;
        } else {
            //Block block = state.getBlock();
            BlockState blockState = worldChunk.setBlockState(pos, state, (flags & Block.MOVED) != 0);
            if (blockState == null) {
                return false;
            } else {
                //BlockState blockState2 = that.getBlockState(pos);
                if ((flags & Block.SKIP_LIGHTING_UPDATES) == 0 && state != blockState && (state.getOpacity(that, pos) != blockState.getOpacity(that, pos) || state.getLuminance() != blockState.getLuminance() || state.hasSidedTransparency() || blockState.hasSidedTransparency())) {
                    //LasertagMod.LOGGER.info("1");
                    that.getProfiler().push("queueCheckLight");
                    that.getChunkManager().getLightingProvider().checkBlock(pos);
                    that.getProfiler().pop();
                }

//                if (blockState2 == state) {
                    if (blockState != state) {
                        //LasertagMod.LOGGER.info("2");
                        that.scheduleBlockRerenderIfNeeded(pos, blockState, state);
                    }

//                    if ((flags & Block.NOTIFY_LISTENERS) != 0 && (!that.isClient || (flags & Block.NO_REDRAW) == 0) && (that.isClient || worldChunk.getLevelType() != null && worldChunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
//                        LasertagMod.LOGGER.info("3");
//                        that.updateListeners(pos, blockState, state, flags);
//                    }

//                    if ((flags & Block.NOTIFY_NEIGHBORS) != 0) {
//                        LasertagMod.LOGGER.info("4");
//                        that.updateNeighbors(pos, blockState.getBlock());
//                        if (!that.isClient && state.hasComparatorOutput()) {
//                            LasertagMod.LOGGER.info("5");
//                            that.updateComparators(pos, block);
//                        }
//                    }

//                    if ((flags & Block.FORCE_STATE) == 0) {
//                        LasertagMod.LOGGER.info("6");
//                        int i = flags & ~(Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS);
//                        blockState.prepare(that, pos, i, 511);
//                        state.updateNeighbors(that, pos, i, 511);
//                        state.prepare(that, pos, i, 511);
//                    }

                    //LasertagMod.LOGGER.info("7");
                    that.onBlockChanged(pos, blockState, state);
//                }

                ((ServerWorld)that).getChunkManager().markForUpdate(pos);

                return true;
            }
        }
    }
}

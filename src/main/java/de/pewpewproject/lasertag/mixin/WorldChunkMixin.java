package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.common.util.IFastChunk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;

/**
 * TODO
 *
 * @author Étienne Muser
 */
@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin implements IFastChunk {
    @Override
    public BlockState fastSetBlockState(BlockPos pos, BlockState state, boolean moved) {

        var that = (WorldChunk)(Object)this;

        int i = pos.getY();
        ChunkSection chunkSection = that.getSection(that.getSectionIndex(i));
        boolean bl = chunkSection.isEmpty();
        if (bl && state.isAir()) {
            return null;
        } else {
            int j = pos.getX() & 15;
            int k = i & 15;
            int l = pos.getZ() & 15;
            BlockState blockState = chunkSection.setBlockState(j, k, l, state);
            if (blockState == state) {
                return null;
            } else {
                Block block = state.getBlock();
                (that.getHeightmap(Heightmap.Type.MOTION_BLOCKING)).trackUpdate(j, i, l, state);
                (that.getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).trackUpdate(j, i, l, state);
                (that.getHeightmap(Heightmap.Type.OCEAN_FLOOR)).trackUpdate(j, i, l, state);
                (that.getHeightmap(Heightmap.Type.WORLD_SURFACE)).trackUpdate(j, i, l, state);
                boolean bl2 = chunkSection.isEmpty();
                if (bl != bl2) {
                    that.getWorld().getChunkManager().getLightingProvider().setSectionStatus(pos, bl2);
                }

                boolean bl3 = blockState.hasBlockEntity();
                if (!that.getWorld().isClient) {
                    blockState.onStateReplaced(that.getWorld(), pos, state, moved);
                } else if (!blockState.isOf(block) && bl3) {
                    that.removeBlockEntity(pos);
                }

                if (!chunkSection.getBlockState(j, k, l).isOf(block)) {
                    return null;
                } else {
                    if (!that.getWorld().isClient) {
                        state.onBlockAdded(that.getWorld(), pos, blockState, moved);
                    }

                    if (state.hasBlockEntity()) {
                        BlockEntity blockEntity = that.getBlockEntity(pos, WorldChunk.CreationType.CHECK);
                        if (blockEntity == null) {
                            blockEntity = ((BlockEntityProvider)block).createBlockEntity(pos, state);
                            if (blockEntity != null) {
                                that.addBlockEntity(blockEntity);
                            }
                        } else {
                            blockEntity.setCachedState(state);
                            that.updateTicker(blockEntity);
                        }
                    }

                    that.setNeedsSaving(true);
                    return blockState;
                }
            }
        }
    }
}

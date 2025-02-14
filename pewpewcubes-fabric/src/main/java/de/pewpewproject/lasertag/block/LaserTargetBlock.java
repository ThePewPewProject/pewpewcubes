package de.pewpewproject.lasertag.block;

import de.pewpewproject.lasertag.block.entity.LaserTargetBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

/**
 * Class to implement the custom behavior of the lasertagret block
 *
 * @author Étienne Muser
 */
public class LaserTargetBlock extends WallMountedBlock implements BlockEntityProvider {
    public LaserTargetBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(FACE, WallMountLocation.WALL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserTargetBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        final float min = 0.1875F;
        final float max = 0.8125F;

        switch (state.get(FACE)) {
            case FLOOR -> { return VoxelShapes.cuboid(min, 0, min, max, max, max); }
            case WALL -> {
                switch (state.get(FACING)) {
                    case NORTH -> { return VoxelShapes.cuboid(min, min, min, max, max, 1); }
                    case EAST -> { return VoxelShapes.cuboid(0, min, min, max, max, max); }
                    case SOUTH -> { return VoxelShapes.cuboid(min, min, 0, max, max, max); }
                    case WEST -> { return VoxelShapes.cuboid(min, min, min, 1, max, max); }
                    default -> { return null; }
                }
            }
            case CEILING -> { return VoxelShapes.cuboid(min, min, min, max, 1.0, max); }
            default -> { return null; }
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}

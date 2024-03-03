package de.pewpewproject.lasertag.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

/**
 * Class to implement the arena divider block
 *
 * @author Étienne Muser
 */
public class ArenaDividerBlock extends HorizontalFacingBlock {
    public ArenaDividerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {

        var facingDirection = state.get(FACING);

        if (facingDirection.getAxis() == Direction.Axis.X) {
            return VoxelShapes.cuboid(0.0, 0.0, 7.0 / 16.0, 1.0, 1.0, 9.0 / 16.0);
        } else {
            return VoxelShapes.cuboid(7.0 / 16.0, 0.0, 0.0, 9.0 / 16.0, 1.0, 1.0);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {

        Direction direction = ctx.getPlayerFacing();
        return this.getDefaultState().with(FACING, direction);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

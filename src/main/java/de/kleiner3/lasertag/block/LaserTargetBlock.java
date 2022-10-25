package de.kleiner3.lasertag.block;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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

    /**
     * Called when this block got hit by a laser ray
     *
     * @param playerEntity The player who hit the target
     */
    public void onHitBy(PlayerEntity playerEntity, BlockEntity blockEntity) {
        // Get the server
        MinecraftServer server = playerEntity.getServer();

        // Sanity check
        if (server != null && blockEntity instanceof LaserTargetBlockEntity) {
            ((LaserTargetBlockEntity)blockEntity).onHitBy(server, playerEntity);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserTargetBlockEntity(pos, state);
    }
}

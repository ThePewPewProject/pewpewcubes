package de.kleiner3.lasertag.block;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;

/**
 * Class to implement the custom behavior of the lasertagret block
 *
 * @author Étienne Muser
 */
public class LaserTargetBlock extends WallMountedBlock {

    public LaserTargetBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState) ((BlockState) ((BlockState) ((BlockState) this.stateManager.getDefaultState()).with(FACING, Direction.NORTH))).with(FACE, WallMountLocation.WALL));
    }

    /**
     * Called when this block got hit by a laser ray
     *
     * @param playerEntity The player who hit the target
     */
    public void onHitBy(PlayerEntity playerEntity) {
        // Get the server
        MinecraftServer server = playerEntity.getServer();

        // Sanity check
        if (server != null) {
            server.onPlayerScored(playerEntity, LasertagConfig.lasertargetHitScore);
            ServerEventSending.sendPlayerScoredSoundEvent((ServerPlayerEntity) playerEntity);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }
}

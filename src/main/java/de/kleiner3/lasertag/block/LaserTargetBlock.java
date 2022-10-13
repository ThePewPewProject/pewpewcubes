package de.kleiner3.lasertag.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;

/**
 * Class to implement the custom behavior of the lasertagret block
 * 
 * @author Étienne Muser
 *
 */
public class LaserTargetBlock extends WallMountedBlock {

	public LaserTargetBlock(Settings settings) {
		super(settings);
		this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH))).with(FACE, WallMountLocation.WALL));
	}
	
	/**
	 * Called when this block got hit by a laser ray
	 * @param playerEntity The player who hit the target
	 */
	public void onHitBy(PlayerEntity playerEntity) {
		// TODO: implement
		playerEntity.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
	}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }
}

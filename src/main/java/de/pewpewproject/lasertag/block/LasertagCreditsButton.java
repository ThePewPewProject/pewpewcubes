package de.pewpewproject.lasertag.block;

import de.pewpewproject.lasertag.block.entity.LasertagCreditsButtonBlockEntity;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * The lasertag credits button
 *
 * @author Étienne Muser
 */
public class LasertagCreditsButton extends AbstractButtonBlock implements BlockEntityProvider {
    protected LasertagCreditsButton(Settings settings) {
        super(false, settings);
    }

    @Override
    protected SoundEvent getClickSound(boolean powered) {
        return powered ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(POWERED)) {
            return ActionResult.CONSUME;
        } else {

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LasertagCreditsButtonBlockEntity lasertagCreditsButtonBlockEntity) {
                lasertagCreditsButtonBlockEntity.openScreen(player);
            }

            this.playClickSound(player, world, pos, true);
            return ActionResult.success(world.isClient);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LasertagCreditsButtonBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}

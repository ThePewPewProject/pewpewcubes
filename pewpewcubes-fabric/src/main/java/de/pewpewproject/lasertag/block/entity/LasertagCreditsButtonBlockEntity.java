package de.pewpewproject.lasertag.block.entity;

import de.pewpewproject.lasertag.entity.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Block entity for the lasertag credits block
 *
 * @author Étienne Muser
 */
public class LasertagCreditsButtonBlockEntity extends BlockEntity {
    public LasertagCreditsButtonBlockEntity(BlockPos pos, BlockState state) {
        super(Entities.LASERTAG_CREDITS_BLOCK_ENTITY, pos, state);
    }

    public void openScreen(PlayerEntity player) {
        if (player.getEntityWorld().isClient) {
            player.openLasertagCreditsScreen(player);
        }
    }
}

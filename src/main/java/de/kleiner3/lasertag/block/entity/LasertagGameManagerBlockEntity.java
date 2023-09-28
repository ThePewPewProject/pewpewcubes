package de.kleiner3.lasertag.block.entity;

import de.kleiner3.lasertag.entity.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Block entity for the game manager block
 *
 * @author Étienne Muser
 */
public class LasertagGameManagerBlockEntity extends BlockEntity {

    public LasertagGameManagerBlockEntity(BlockPos pos, BlockState state) {
        super(Entities.LASERTAG_GAME_MANAGER_BLOCK_ENTITY, pos, state);
    }

    public void openScreen(PlayerEntity player) {
        if (player.getEntityWorld().isClient) {
            player.openLasertagGameManagerScreen(player);
        }
    }
}

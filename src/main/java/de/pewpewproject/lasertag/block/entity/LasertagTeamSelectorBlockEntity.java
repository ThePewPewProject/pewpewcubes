package de.pewpewproject.lasertag.block.entity;

import de.pewpewproject.lasertag.entity.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Block entity for the team selector block
 *
 * @author Étienne Muser
 */
public class LasertagTeamSelectorBlockEntity extends BlockEntity {
    public LasertagTeamSelectorBlockEntity(BlockPos pos, BlockState state) {
        super(Entities.LASERTAG_TEAM_SELECTOR_BLOCK_ENTITY, pos, state);
    }

    public void openScreen(PlayerEntity player) {
        if (player.getEntityWorld().isClient) {
            player.openLasertagTeamSelectorScreen(player);
        }
    }
}

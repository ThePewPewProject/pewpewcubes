package de.pewpewproject.lasertag.block.entity;

import de.pewpewproject.lasertag.entity.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

/**
 * The block entity for the lasertag start game button to be able to use custom entity renderers to render
 * the "Start Game" label.
 *
 * @author Étienne Muser
 */
public class LasertagStartGameButtonBlockEntity extends BlockEntity {
    public LasertagStartGameButtonBlockEntity(BlockPos pos, BlockState state) {
        super(Entities.LASERTAG_START_GAME_BUTTON_ENTITY, pos, state);
    }
}

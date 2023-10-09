package de.kleiner3.lasertag.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * @author Étienne Muser
 */
public interface LasertagBlockEntityTicker<T extends BlockEntity>{
    void tick(ServerWorld world, T blockEntity);
}

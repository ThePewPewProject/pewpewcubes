package de.kleiner3.lasertag.block.entity;

import net.minecraft.server.world.ServerWorld;

/**
 * @author Étienne Muser
 */
public interface LasertagCustomBlockTickable {
    void serverTick(ServerWorld world);
}

package de.kleiner3.lasertag.entity;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.Blocks;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Class for registering all entities
 *
 * @author Étienne Muser
 */
public class Entities {
    // Create instances for all block entities
    public static BlockEntityType<LaserTargetBlockEntity> LASER_TARGET_ENTITY;

    // Register all entities
    public static EntityType<LaserRayEntity> LASER_RAY;

    public static void register() {
        LASER_RAY = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(LasertagMod.ID, "laser_ray_entity"),
                FabricEntityTypeBuilder.<LaserRayEntity>create(SpawnGroup.MISC, LaserRayEntity::new).build());

        LASER_TARGET_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(LasertagMod.ID, "lasertarget_entity"),
                FabricBlockEntityTypeBuilder.create(LaserTargetBlockEntity::new, Blocks.LASER_TARGET).build()
        );
    }
}

package de.kleiner3.lasertag.entity;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.Blocks;
import de.kleiner3.lasertag.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
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
    public static BlockEntityType<LasertagGameManagerBlockEntity> LASERTAG_GAME_MANAGER_BLOCK_ENTITY;
    public static BlockEntityType<LasertagTeamSelectorBlockEntity> LASERTAG_TEAM_SELECTOR_BLOCK_ENTITY;
    public static BlockEntityType<LasertagCreditsButtonBlockEntity> LASERTAG_CREDITS_BLOCK_ENTITY;
    public static BlockEntityType<LasertagStartGameButtonBlockEntity> LASERTAG_START_GAME_BUTTON_ENTITY;
    public static BlockEntityType<LasertagTeamZoneGeneratorBlockEntity> LASERTAG_TEAM_ZONE_GENERATOR_BLOCK_ENTITY;

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
                FabricBlockEntityTypeBuilder.create(LaserTargetBlockEntity::new, Blocks.LASER_TARGET).build());

        LASERTAG_GAME_MANAGER_BLOCK_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(LasertagMod.ID, "lasertag_game_manager_block_entity"),
                FabricBlockEntityTypeBuilder.create(LasertagGameManagerBlockEntity::new, Blocks.LASERTAG_GAME_MANAGER_BLOCK).build());

        LASERTAG_TEAM_SELECTOR_BLOCK_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(LasertagMod.ID, "lasertag_team_selector_block_entity"),
                FabricBlockEntityTypeBuilder.create(LasertagTeamSelectorBlockEntity::new, Blocks.LASERTAG_TEAM_SELECTOR_BLOCK).build());

        LASERTAG_CREDITS_BLOCK_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(LasertagMod.ID, "lasertag_credits_button_block_entity"),
                FabricBlockEntityTypeBuilder.create(LasertagCreditsButtonBlockEntity::new, Blocks.LASERTAG_CREDITS_BUTTON).build());

        LASERTAG_START_GAME_BUTTON_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(LasertagMod.ID, "lasertag_start_game_button_block_entity"),
                FabricBlockEntityTypeBuilder.create(LasertagStartGameButtonBlockEntity::new, Blocks.LASERTAG_START_GAME_BUTTON).build());

        LASERTAG_TEAM_ZONE_GENERATOR_BLOCK_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(LasertagMod.ID, "lasertag_team_zone_generator_block_entity"),
                FabricBlockEntityTypeBuilder.create(LasertagTeamZoneGeneratorBlockEntity::new, Blocks.LASERTAG_TEAM_ZONE_GENERATOR_BLOCK).build());
    }
}

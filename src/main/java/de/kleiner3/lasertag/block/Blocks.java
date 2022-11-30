package de.kleiner3.lasertag.block;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Blocks {
    // Create instances for all blocks
    public static final Block ARENA_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_STAIRS = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_SLAB = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_DIVIDER = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.STONE).requiresTool().noCollision().strength(0.5f));
    public static final Block LASER_TARGET = new LaserTargetBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).luminance(2));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block"), ARENA_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_stairs"), ARENA_BLOCK_STAIRS);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_slab"), ARENA_BLOCK_SLAB);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_divider"), ARENA_DIVIDER);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_pressure_plate"), ARENA_PRESSURE_PLATE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "lasertarget"), LASER_TARGET);
    }
}

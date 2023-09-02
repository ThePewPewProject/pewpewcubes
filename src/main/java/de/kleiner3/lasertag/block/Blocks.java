package de.kleiner3.lasertag.block;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Class to register all blocks
 *
 * @author Étienne Muser
 */
public class Blocks {
    // Create instances for all blocks
    public static final Block ARENA_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_DARK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_STAIRS = new StairsBlock(ARENA_BLOCK.getDefaultState(), FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_SLAB = new SlabBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_DIVIDER = new ArenaDividerBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.STONE).requiresTool().noCollision().strength(0.5f));
    public static final Block LASER_TARGET = new LaserTargetBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).luminance(2));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block"), ARENA_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_dark"), ARENA_BLOCK_DARK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_stairs"), ARENA_BLOCK_STAIRS);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_slab"), ARENA_BLOCK_SLAB);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_divider"), ARENA_DIVIDER);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_pressure_plate"), ARENA_PRESSURE_PLATE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "lasertarget"), LASER_TARGET);
    }
}

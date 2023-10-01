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
    // Arena blocks
    public static final Block ARENA_BLOCK_ORANGE= new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_PINK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_BLUE = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_YELLOW = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_GREEN = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_PURPLE = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_RED = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    // Arena block pillars
    public static final Block ARENA_PILLAR_BLOCK_ORANGE = new PillarBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PILLAR_BLOCK_PINK = new PillarBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PILLAR_BLOCK_BLUE = new PillarBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PILLAR_BLOCK_YELLOW = new PillarBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PILLAR_BLOCK_GREEN = new PillarBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PILLAR_BLOCK_PURPLE = new PillarBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PILLAR_BLOCK_RED = new PillarBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    // Arena block stairs
    public static final Block ARENA_BLOCK_STAIRS_ORANGE = new StairsBlock(ARENA_BLOCK_YELLOW.getDefaultState(), FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_STAIRS_PINK = new StairsBlock(ARENA_BLOCK_YELLOW.getDefaultState(), FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_STAIRS_BLUE = new StairsBlock(ARENA_BLOCK_YELLOW.getDefaultState(), FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_STAIRS_YELLOW = new StairsBlock(ARENA_BLOCK_YELLOW.getDefaultState(), FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_STAIRS_GREEN = new StairsBlock(ARENA_BLOCK_YELLOW.getDefaultState(), FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_STAIRS_PURPLE = new StairsBlock(ARENA_BLOCK_YELLOW.getDefaultState(), FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_STAIRS_RED = new StairsBlock(ARENA_BLOCK_YELLOW.getDefaultState(), FabricBlockSettings.of(Material.METAL).strength(4.0f));

    // Arena block slabs
    public static final Block ARENA_BLOCK_SLAB_ORANGE = new SlabBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_SLAB_PINK = new SlabBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_SLAB_BLUE = new SlabBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_SLAB_YELLOW = new SlabBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_SLAB_GREEN = new SlabBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_SLAB_PURPLE = new SlabBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_SLAB_RED = new SlabBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    // Arena dividers
    public static final Block ARENA_DIVIDER_ORANGE = new ArenaDividerBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_DIVIDER_PINK = new ArenaDividerBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_DIVIDER_BLUE = new ArenaDividerBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_DIVIDER_YELLOW = new ArenaDividerBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_DIVIDER_GREEN = new ArenaDividerBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_DIVIDER_PURPLE = new ArenaDividerBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_DIVIDER_RED = new ArenaDividerBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    // Blocks without color
    public static final Block ARENA_BLOCK_DARK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.STONE).requiresTool().noCollision().strength(0.5f));
    public static final Block LASER_TARGET = new LaserTargetBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).luminance(2));

    // Interactable blocks
    public static final Block LASERTAG_GAME_MANAGER_BLOCK = new LasertagGameManagerBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block LASERTAG_TEAM_SELECTOR_BLOCK = new LasertagTeamSelectorBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block LASERTAG_CREDITS_BUTTON = new LasertagCreditsButton(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_orange"), ARENA_BLOCK_ORANGE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_pink"), ARENA_BLOCK_PINK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_blue"), ARENA_BLOCK_BLUE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_yellow"), ARENA_BLOCK_YELLOW);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_green"), ARENA_BLOCK_GREEN);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_purple"), ARENA_BLOCK_PURPLE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_red"), ARENA_BLOCK_RED);

        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_pillar_block_orange"), ARENA_PILLAR_BLOCK_ORANGE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_pillar_block_pink"), ARENA_PILLAR_BLOCK_PINK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_pillar_block_blue"), ARENA_PILLAR_BLOCK_BLUE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_pillar_block_yellow"), ARENA_PILLAR_BLOCK_YELLOW);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_pillar_block_green"), ARENA_PILLAR_BLOCK_GREEN);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_pillar_block_purple"), ARENA_PILLAR_BLOCK_PURPLE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_pillar_block_red"), ARENA_PILLAR_BLOCK_RED);

        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_stairs_orange"), ARENA_BLOCK_STAIRS_ORANGE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_stairs_pink"), ARENA_BLOCK_STAIRS_PINK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_stairs_blue"), ARENA_BLOCK_STAIRS_BLUE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_stairs_yellow"), ARENA_BLOCK_STAIRS_YELLOW);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_stairs_green"), ARENA_BLOCK_STAIRS_GREEN);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_stairs_purple"), ARENA_BLOCK_STAIRS_PURPLE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_stairs_red"), ARENA_BLOCK_STAIRS_RED);

        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_slab_orange"), ARENA_BLOCK_SLAB_ORANGE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_slab_pink"), ARENA_BLOCK_SLAB_PINK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_slab_blue"), ARENA_BLOCK_SLAB_BLUE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_slab_yellow"), ARENA_BLOCK_SLAB_YELLOW);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_slab_green"), ARENA_BLOCK_SLAB_GREEN);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_slab_purple"), ARENA_BLOCK_SLAB_PURPLE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_slab_red"), ARENA_BLOCK_SLAB_RED);

        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_divider_orange"), ARENA_DIVIDER_ORANGE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_divider_pink"), ARENA_DIVIDER_PINK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_divider_blue"), ARENA_DIVIDER_BLUE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_divider_yellow"), ARENA_DIVIDER_YELLOW);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_divider_green"), ARENA_DIVIDER_GREEN);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_divider_purple"), ARENA_DIVIDER_PURPLE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_divider_red"), ARENA_DIVIDER_RED);

        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_block_dark"), ARENA_BLOCK_DARK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "arena_pressure_plate"), ARENA_PRESSURE_PLATE);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "lasertarget"), LASER_TARGET);

        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "game_manager"), LASERTAG_GAME_MANAGER_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "team_selector"), LASERTAG_TEAM_SELECTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(LasertagMod.ID, "lasertag_credits_button"), LASERTAG_CREDITS_BUTTON);
    }
}

package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.Blocks;
import de.kleiner3.lasertag.item.material.LaserVestMaterial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

/**
 * Class for registering all items
 *
 * @author Étienne Muser
 */
public class Items {

    // Create instances for all materials
    public static final ArmorMaterial LASERVEST_MATERIAL = new LaserVestMaterial();

    // Create instances for all items
    public static final Item LASERTAG_WEAPON = new LasertagWeaponItem(new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP).maxCount(1).rarity(Rarity.EPIC));
    public static final Item LASERTAG_VEST = new LasertagVestItem(LASERVEST_MATERIAL, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP).rarity(Rarity.EPIC));


    public static void register() {

        // Block items
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_orange"),
                new BlockItem(Blocks.ARENA_BLOCK_ORANGE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_pink"),
                new BlockItem(Blocks.ARENA_BLOCK_PINK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_blue"),
                new BlockItem(Blocks.ARENA_BLOCK_BLUE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_yellow"),
                new BlockItem(Blocks.ARENA_BLOCK_YELLOW, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_green"),
                new BlockItem(Blocks.ARENA_BLOCK_GREEN, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_purple"),
                new BlockItem(Blocks.ARENA_BLOCK_PURPLE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_red"),
                new BlockItem(Blocks.ARENA_BLOCK_RED, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));

        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_pillar_block_orange"),
                new BlockItem(Blocks.ARENA_PILLAR_BLOCK_ORANGE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_pillar_block_pink"),
                new BlockItem(Blocks.ARENA_PILLAR_BLOCK_PINK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_pillar_block_blue"),
                new BlockItem(Blocks.ARENA_PILLAR_BLOCK_BLUE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_pillar_block_yellow"),
                new BlockItem(Blocks.ARENA_PILLAR_BLOCK_YELLOW, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_pillar_block_green"),
                new BlockItem(Blocks.ARENA_PILLAR_BLOCK_GREEN, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_pillar_block_purple"),
                new BlockItem(Blocks.ARENA_PILLAR_BLOCK_PURPLE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_pillar_block_red"),
                new BlockItem(Blocks.ARENA_PILLAR_BLOCK_RED, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));

        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_stairs_orange"),
                new BlockItem(Blocks.ARENA_BLOCK_STAIRS_ORANGE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_stairs_pink"),
                new BlockItem(Blocks.ARENA_BLOCK_STAIRS_PINK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_stairs_blue"),
                new BlockItem(Blocks.ARENA_BLOCK_STAIRS_BLUE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_stairs_yellow"),
                new BlockItem(Blocks.ARENA_BLOCK_STAIRS_YELLOW, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_stairs_green"),
                new BlockItem(Blocks.ARENA_BLOCK_STAIRS_GREEN, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_stairs_purple"),
                new BlockItem(Blocks.ARENA_BLOCK_STAIRS_PURPLE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_stairs_red"),
                new BlockItem(Blocks.ARENA_BLOCK_STAIRS_RED, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));

        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_slab_orange"),
                new BlockItem(Blocks.ARENA_BLOCK_SLAB_ORANGE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_slab_pink"),
                new BlockItem(Blocks.ARENA_BLOCK_SLAB_PINK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_slab_blue"),
                new BlockItem(Blocks.ARENA_BLOCK_SLAB_BLUE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_slab_yellow"),
                new BlockItem(Blocks.ARENA_BLOCK_SLAB_YELLOW, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_slab_green"),
                new BlockItem(Blocks.ARENA_BLOCK_SLAB_GREEN, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_slab_purple"),
                new BlockItem(Blocks.ARENA_BLOCK_SLAB_PURPLE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_slab_red"),
                new BlockItem(Blocks.ARENA_BLOCK_SLAB_RED, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));

        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_divider_orange"),
                new BlockItem(Blocks.ARENA_DIVIDER_ORANGE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_divider_pink"),
                new BlockItem(Blocks.ARENA_DIVIDER_PINK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_divider_blue"),
                new BlockItem(Blocks.ARENA_DIVIDER_BLUE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_divider_yellow"),
                new BlockItem(Blocks.ARENA_DIVIDER_YELLOW, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_divider_green"),
                new BlockItem(Blocks.ARENA_DIVIDER_GREEN, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_divider_purple"),
                new BlockItem(Blocks.ARENA_DIVIDER_PURPLE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_divider_red"),
                new BlockItem(Blocks.ARENA_DIVIDER_RED, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));

        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_dark"),
                new BlockItem(Blocks.ARENA_BLOCK_DARK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_pressure_plate"),
                new BlockItem(Blocks.ARENA_PRESSURE_PLATE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "lasertarget"),
                new BlockItem(Blocks.LASER_TARGET, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));

        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "game_manager"),
                new BlockItem(Blocks.LASERTAG_GAME_MANAGER_BLOCK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "team_selector"),
                new BlockItem(Blocks.LASERTAG_TEAM_SELECTOR_BLOCK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "lasertag_credits_button"),
                new BlockItem(Blocks.LASERTAG_CREDITS_BUTTON, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "lasertag_start_game_button"),
                new BlockItem(Blocks.LASERTAG_START_GAME_BUTTON, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "team_zone_generator"),
                new BlockItem(Blocks.LASERTAG_TEAM_ZONE_GENERATOR_BLOCK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));

        // Normal items
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "lasertag_weapon"), LASERTAG_WEAPON);
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "lasertag_vest"), LASERTAG_VEST);
    }
}

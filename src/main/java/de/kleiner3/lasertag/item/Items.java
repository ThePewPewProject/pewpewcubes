package de.kleiner3.lasertag.item;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.Blocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

/**
 * Class for registerin all items
 *
 * @author Étienne Muser
 */
public class Items {
    // Create instances for all items
    public static final Item LASERTAG_WEAPON = new LasertagWeaponItem(new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP).maxCount(1).rarity(Rarity.EPIC));
    public static final Item LASERTAG_VEST = new LasertagVestItem(ArmorMaterials.LEATHER, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP).rarity(Rarity.EPIC));


    public static void register() {
        // Block items
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block"),
                new BlockItem(Blocks.ARENA_BLOCK, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_stairs"),
                new BlockItem(Blocks.ARENA_BLOCK_STAIRS, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_slab"),
                new BlockItem(Blocks.ARENA_BLOCK_SLAB, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_divider"),
                new BlockItem(Blocks.ARENA_DIVIDER, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "arena_block_pressure_plate"),
                new BlockItem(Blocks.ARENA_PRESSURE_PLATE, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "lasertarget"),
                new BlockItem(Blocks.LASER_TARGET, new FabricItemSettings().group(ItemGroups.LASERTAG_ITEM_GROUP)));

        // Normal items
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "lasertag_weapon"), LASERTAG_WEAPON);
        Registry.register(Registry.ITEM, new Identifier(LasertagMod.ID, "lasertag_vest"), LASERTAG_VEST);
    }
}

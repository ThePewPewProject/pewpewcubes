package de.kleiner3.lasertag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.kleiner3.lasertag.block.LaserTargetBlock;
import de.kleiner3.lasertag.item.LasertagItemGroupBuilder;
import de.kleiner3.lasertag.item.LasertagVest;
import de.kleiner3.lasertag.item.LasertagWeaponItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * This class initializes the mod.
 * 
 * @author Étienne Muser
 *
 */
public class LasertagMod implements ModInitializer {

	// Log4j logger instance for this mod
	public static final Logger LOGGER = LoggerFactory.getLogger("lasertag-mod");

	// Item group for lasertag items
	public static final ItemGroup LASERTAG_ITEM_GROUP = LasertagItemGroupBuilder.build();
	
	// Create Instances for all blocks
	public static final Block ARENA_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
	public static final Block ARENA_BLOCK_STAIRS = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
	public static final Block ARENA_BLOCK_SLAB = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
	public static final Block ARENA_DIVIDER = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
	public static final Block ARENA_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.STONE).requiresTool().noCollision().strength(0.5f));
	public static final Block LASER_TARGET = new LaserTargetBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));

	// Create Instances for all items
	public static final Item LASERTAG_WEAPON = new LasertagWeaponItem(new FabricItemSettings().group(LASERTAG_ITEM_GROUP));
	public static final Item LASERTAG_VEST = new LasertagVest(ArmorMaterials.LEATHER, EquipmentSlot.CHEST, new FabricItemSettings().group(LASERTAG_ITEM_GROUP));
	
	@Override
	public void onInitialize() {
		// Register all blocks
		Registry.register(Registry.BLOCK, new Identifier("lasertag", "arena_block"), ARENA_BLOCK);
		Registry.register(Registry.BLOCK, new Identifier("lasertag", "arena_block_stairs"), ARENA_BLOCK_STAIRS);
		Registry.register(Registry.BLOCK, new Identifier("lasertag", "arena_block_slab"), ARENA_BLOCK_SLAB);
		Registry.register(Registry.BLOCK, new Identifier("lasertag", "arena_divider"), ARENA_DIVIDER);
		Registry.register(Registry.BLOCK, new Identifier("lasertag", "arena_block_pressure_plate"), ARENA_PRESSURE_PLATE);
		Registry.register(Registry.BLOCK, new Identifier("lasertag", "lasertarget"), LASER_TARGET);
		
		// Register all block items
		Registry.register(Registry.ITEM, new Identifier("lasertag", "arena_block"),
				new BlockItem(ARENA_BLOCK, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
		Registry.register(Registry.ITEM, new Identifier("lasertag", "arena_block_stairs"), 
				new BlockItem(ARENA_BLOCK_STAIRS, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
		Registry.register(Registry.ITEM, new Identifier("lasertag", "arena_block_slab"), 
				new BlockItem(ARENA_BLOCK_SLAB, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
		Registry.register(Registry.ITEM, new Identifier("lasertag", "arena_divider"), 
				new BlockItem(ARENA_DIVIDER, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
		Registry.register(Registry.ITEM, new Identifier("lasertag", "arena_block_pressure_plate"), 
				new BlockItem(ARENA_PRESSURE_PLATE, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
		Registry.register(Registry.ITEM, new Identifier("lasertag", "lasertarget"), 
				new BlockItem(LASER_TARGET, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
		
		// Register all items
		Registry.register(Registry.ITEM, new Identifier("lasertag", "lasertag_weapon"), LASERTAG_WEAPON);
		Registry.register(Registry.ITEM, new Identifier("lasertag", "lasertag_vest"), LASERTAG_VEST);
	}
}

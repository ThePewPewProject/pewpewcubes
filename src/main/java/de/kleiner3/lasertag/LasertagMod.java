package de.kleiner3.lasertag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class initializes the mod.
 * 
 * @author Étienne Muser
 *
 */
public class LasertagMod implements ModInitializer {

	// Log4j logger instance for this mod
	public static final Logger LOGGER = LoggerFactory.getLogger("lasertag-mod");

	// Example block. See: https://fabricmc.net/wiki/tutorial:blocks
	public static final Block EXAMPLE_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));

	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("lasertag", "example_block"), EXAMPLE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("tutorial", "example_block"),
				new BlockItem(EXAMPLE_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));

		LOGGER.info("Lasertag mod initialized!");
	}
}

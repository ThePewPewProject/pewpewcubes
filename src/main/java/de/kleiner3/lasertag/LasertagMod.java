package de.kleiner3.lasertag;

import de.kleiner3.lasertag.block.LaserTargetBlock;
import de.kleiner3.lasertag.command.CommandInitializer;
import de.kleiner3.lasertag.entity.LaserRayEntity;
import de.kleiner3.lasertag.item.LasertagItemGroupBuilder;
import de.kleiner3.lasertag.item.LasertagVestItem;
import de.kleiner3.lasertag.item.LasertagWeaponItem;
import de.kleiner3.lasertag.types.Colors;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * This class initializes the mod.
 *
 * @author Étienne Muser
 */
public class LasertagMod implements ModInitializer {

    /**
     * The mod id of this mod
     */
    public static final String ID = "lasertag";

    // Log4j logger instance for this mod
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    // Item group for lasertag items
    public static final ItemGroup LASERTAG_ITEM_GROUP = LasertagItemGroupBuilder.build();

    // Create instances for all blocks
    public static final Block ARENA_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_STAIRS = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_BLOCK_SLAB = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_DIVIDER = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block ARENA_PRESSURE_PLATE = new PressurePlateBlock(PressurePlateBlock.ActivationRule.MOBS, AbstractBlock.Settings.of(Material.STONE).requiresTool().noCollision().strength(0.5f));
    public static final Block LASER_TARGET = new LaserTargetBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).luminance(2));

    // Create instances for all block entities


    // Create instances for all items
    public static final ArrayList<Item> LASERTAG_WEAPONS = new ArrayList<>();
    public static final ArrayList<Item> LASERTAG_VESTS = new ArrayList<>();

    // Register all entities
    public static final EntityType<LaserRayEntity> LASER_RAY = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(ID, "laser_ray_entity"),
            FabricEntityTypeBuilder.<LaserRayEntity>create(SpawnGroup.MISC, LaserRayEntity::new).dimensions(EntityDimensions.fixed(.1F, .1F)).build());

    @Override
    public void onInitialize() {
        // ===== Register all blocks ====================
        Registry.register(Registry.BLOCK, new Identifier(ID, "arena_block"), ARENA_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(ID, "arena_block_stairs"), ARENA_BLOCK_STAIRS);
        Registry.register(Registry.BLOCK, new Identifier(ID, "arena_block_slab"), ARENA_BLOCK_SLAB);
        Registry.register(Registry.BLOCK, new Identifier(ID, "arena_divider"), ARENA_DIVIDER);
        Registry.register(Registry.BLOCK, new Identifier(ID, "arena_block_pressure_plate"), ARENA_PRESSURE_PLATE);
        Registry.register(Registry.BLOCK, new Identifier(ID, "lasertarget"), LASER_TARGET);

        // ===== Register all block items ====================
        Registry.register(Registry.ITEM, new Identifier(ID, "arena_block"),
                new BlockItem(ARENA_BLOCK, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(ID, "arena_block_stairs"),
                new BlockItem(ARENA_BLOCK_STAIRS, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(ID, "arena_block_slab"),
                new BlockItem(ARENA_BLOCK_SLAB, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(ID, "arena_divider"),
                new BlockItem(ARENA_DIVIDER, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(ID, "arena_block_pressure_plate"),
                new BlockItem(ARENA_PRESSURE_PLATE, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier(ID, "lasertarget"),
                new BlockItem(LASER_TARGET, new FabricItemSettings().group(LASERTAG_ITEM_GROUP)));

        // ===== Register all items ====================

        // For every default color
        for (Colors color : Colors.values()) {
            // Create new instance of a lasertag weapon
            LasertagWeaponItem weapon = new LasertagWeaponItem(new FabricItemSettings().group(LASERTAG_ITEM_GROUP).maxCount(1), color);

            // Register the weapon
            Registry.register(Registry.ITEM, new Identifier(ID, "lasertag_weapon_" + color.name().toLowerCase()), weapon);

            // Save in static lasertag weapons list
            LASERTAG_WEAPONS.add(weapon);

            // Create new instance of a lasertag vest
            LasertagVestItem vest = new LasertagVestItem(ArmorMaterials.LEATHER, new FabricItemSettings().group(LASERTAG_ITEM_GROUP), color);

            // Register the vest
            Registry.register(Registry.ITEM, new Identifier(ID, "lasertag_vest_" + color.name().toLowerCase()), vest);

            // Save in static lasertag vest list
            LASERTAG_VESTS.add(vest);
        }

        // ===== Register commands =====================
        CommandInitializer.initCommands();

        // ===== Listen to events ======================
    }
}

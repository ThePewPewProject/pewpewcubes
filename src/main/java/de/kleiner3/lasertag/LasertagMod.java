package de.kleiner3.lasertag;

import de.kleiner3.lasertag.block.LaserTargetBlock;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.command.CommandInitializer;
import de.kleiner3.lasertag.entity.LaserRayEntity;
import de.kleiner3.lasertag.item.LasertagItemGroupBuilder;
import de.kleiner3.lasertag.item.LasertagVestItem;
import de.kleiner3.lasertag.item.LasertagWeaponItem;
import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.worldgen.chunkgen.JungleArenaChunkGenerator;
import de.kleiner3.lasertag.worldgen.chunkgen.VoidChunkGenerator;
import de.kleiner3.lasertag.worldgen.structure.StructureResourceManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * This class initializes the mod.
 *
 * @author Étienne Muser
 */
public class LasertagMod implements ModInitializer {
    // TODO: This mod somehow disables command line input

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
    public static final BlockEntityType<LaserTargetBlockEntity> LASER_TARGET_ENTITY = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier(ID, "lasertarget_entity"),
            FabricBlockEntityTypeBuilder.create(LaserTargetBlockEntity::new, LASER_TARGET).build()
    );

    // Create instances for all items
    public static final Item LASERTAG_WEAPON = new LasertagWeaponItem(new FabricItemSettings().group(LASERTAG_ITEM_GROUP).maxCount(1).rarity(Rarity.EPIC));
    public static final Item LASERTAG_VEST = new LasertagVestItem(ArmorMaterials.LEATHER, new FabricItemSettings().group(LASERTAG_ITEM_GROUP).rarity(Rarity.EPIC));

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
        Registry.register(Registry.ITEM, new Identifier(ID, "lasertag_weapon"), LASERTAG_WEAPON);
        Registry.register(Registry.ITEM, new Identifier(ID, "lasertag_vest"), LASERTAG_VEST);

        // ===== Register commands =====================
        CommandInitializer.initCommands();

        // ===== Listen to events ======================
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LasertagConfig.syncToPlayer(handler.getPlayer());
            Colors.syncTeamsToClient(handler.getPlayer());
            server.syncTeamsAndScoresToPlayer(handler.getPlayer());
        });
        // TODO: Reset HUD on disconnect/leave world

        // ===== Register chunk generators =============
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier(ID, "void_chunk_generator"), VoidChunkGenerator.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier(ID, "jungle_arena_chunk_generator"), JungleArenaChunkGenerator.CODEC);

        // ===== Register resource manager =============
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(ID, "lasertag_structure_resource_manager");
            }

            @Override
            public void reload(ResourceManager manager) {
                var resources = manager.findResources("structures", path -> path.getPath().endsWith(".nbt"));
                for(var entry : resources.entrySet()) {
                    if (entry.getKey().getNamespace().equals(ID) == false) {
                        continue;
                    }

                    STRUCTURE_RESOURCE_MANAGER.put(entry.getKey(), entry.getValue());
                }
            }
        });
    }

    public static final String configFolderPath = FabricLoader.getInstance().getConfigDir() + "\\lasertag";

    public static final StructureResourceManager STRUCTURE_RESOURCE_MANAGER = new StructureResourceManager();
}

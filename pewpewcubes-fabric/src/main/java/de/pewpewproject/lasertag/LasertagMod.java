package de.pewpewproject.lasertag;

import de.pewpewproject.lasertag.block.Blocks;
import de.pewpewproject.lasertag.client.SoundEvents;
import de.pewpewproject.lasertag.command.Commands;
import de.pewpewproject.lasertag.entity.Entities;
import de.pewpewproject.lasertag.events.EventListeners;
import de.pewpewproject.lasertag.item.Items;
import de.pewpewproject.lasertag.networking.ServerNetworkingHandlers;
import de.pewpewproject.lasertag.resource.ResourceManagers;
import de.pewpewproject.lasertag.worldgen.chunkgen.ChunkGenerators;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib3.GeckoLib;

import java.nio.file.Path;

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

    /**
     * Log4j logger instance for this mod
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    /**
     * The file path to the config folder of this mod
     */
    public static final Path configFolderPath = FabricLoader.getInstance().getConfigDir().resolve("lasertag");

    @Override
    public void onInitialize() {
        // Initialize geckolib
        GeckoLib.initialize();

        // Register all blocks
        Blocks.register();

        // Register all items
        Items.register();

        // Register all entities
        Entities.register();

        // Register commands
        Commands.register();

        // Listen to events
        EventListeners.register();

        // Register chunk generators
        ChunkGenerators.register();

        // Register resource manager
        ResourceManagers.register();

        // Register networking handlers
        ServerNetworkingHandlers.register();

        // Register sound events
        SoundEvents.register();
    }
}

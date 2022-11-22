package de.kleiner3.lasertag;

import de.kleiner3.lasertag.block.Blocks;
import de.kleiner3.lasertag.command.Commands;
import de.kleiner3.lasertag.events.EventListeners;
import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.resource.ResourceManagers;
import de.kleiner3.lasertag.worldgen.chunkgen.ChunkGenerators;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * Log4j logger instance for this mod
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    /**
     * The file path to the config folder of this mod
     */
    public static final String configFolderPath = FabricLoader.getInstance().getConfigDir() + "\\lasertag";

    @Override
    public void onInitialize() {
        // Register all blocks
        Blocks.register();

        // Register all items
        Items.register();

        // Register commands
        Commands.register();

        // Listen to events
        EventListeners.register();

        // Register chunk generators
        ChunkGenerators.register();

        // Register resource manager
        ResourceManagers.register();
    }
}

package de.pewpewproject.lasertag;

import de.pewpewproject.lasertag.block.BlockRenderLayers;
import de.pewpewproject.lasertag.block.entity.BlockEntityRenderers;
import de.pewpewproject.lasertag.block.models.BlockModelProvider;
import de.pewpewproject.lasertag.client.KeyBindings;
import de.pewpewproject.lasertag.client.hud.HudRenderers;
import de.pewpewproject.lasertag.command.ClientCommands;
import de.pewpewproject.lasertag.entity.render.EntityRenderers;
import de.pewpewproject.lasertag.entity.render.armor.ArmorRenderers;
import de.pewpewproject.lasertag.item.render.ItemRenderers;
import de.pewpewproject.lasertag.networking.ClientNetworkingHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

/**
 * Initializes the client side of the mod
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public class LasertagModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register entity renderers
        EntityRenderers.register();

        // Register item renderers
        ItemRenderers.register();

        // Register block entity renderers
        BlockEntityRenderers.register();

        // Register packet recievers
        ClientNetworkingHandlers.register();

        // Register HUD Overlays
        HudRenderers.register();

        // Register armor renderers
        ArmorRenderers.register();

        // Register commands
        ClientCommands.register();

        // Register block render layer handler
        BlockRenderLayers.register();

        // Register key bindings
        KeyBindings.register();

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new BlockModelProvider());
    }

}

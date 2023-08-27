package de.kleiner3.lasertag;

import de.kleiner3.lasertag.block.BlockRenderLayers;
import de.kleiner3.lasertag.block.models.BlockModelProvider;
import de.kleiner3.lasertag.client.ColorProviders;
import de.kleiner3.lasertag.client.hud.HudRenderers;
import de.kleiner3.lasertag.command.ClientCommands;
import de.kleiner3.lasertag.entity.render.EntityRenderers;
import de.kleiner3.lasertag.entity.render.armor.ArmorRenderers;
import de.kleiner3.lasertag.networking.ClientNetworkingHandlers;
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

        // Register packet recievers
        ClientNetworkingHandlers.register();

        // Register HUD Overlays
        HudRenderers.register();

        // Register color providers
        ColorProviders.register();

        // Register armor renderers
        ArmorRenderers.register();

        // Register commands
        ClientCommands.register();

        // Register block render layer handler
        BlockRenderLayers.register();

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new BlockModelProvider());
    }

}

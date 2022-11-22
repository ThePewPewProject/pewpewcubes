package de.kleiner3.lasertag;

import de.kleiner3.lasertag.client.ColorProviders;
import de.kleiner3.lasertag.client.HudRenderers;
import de.kleiner3.lasertag.command.ClientCommands;
import de.kleiner3.lasertag.entity.render.EntityRenderers;
import de.kleiner3.lasertag.entity.render.armor.ArmorRenderers;
import de.kleiner3.lasertag.networking.NetworkingHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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
        NetworkingHandlers.register();

        // Register HUD Overlays
        HudRenderers.register();

        // Register color providers
        ColorProviders.register();

        // Register armor renderers
        ArmorRenderers.register();

        // Register commands
        ClientCommands.register();
    }

}

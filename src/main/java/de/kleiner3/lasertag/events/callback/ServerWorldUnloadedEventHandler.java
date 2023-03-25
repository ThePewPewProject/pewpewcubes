package de.kleiner3.lasertag.events.callback;

import de.kleiner3.lasertag.client.hud.LasertagHudOverlay;
import de.kleiner3.lasertag.client.hud.LasertagHudRenderConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

/**
 * Event handler for the server world unloaded event
 *
 * @author Étienne Muser
 */
public class ServerWorldUnloadedEventHandler {
    public static void onServerWorldUnloaded(MinecraftServer minecraftServer, ServerWorld serverWorld) {
        // Dispose old render config
        LasertagHudOverlay.renderData.dispose();

        // Set new render config
        LasertagHudOverlay.renderData = new LasertagHudRenderConfig();
    }
}

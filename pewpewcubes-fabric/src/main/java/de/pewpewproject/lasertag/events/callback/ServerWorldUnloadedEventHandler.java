package de.pewpewproject.lasertag.events.callback;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

/**
 * Event handler for the server world unloaded event
 *
 * @author Étienne Muser
 */
public class ServerWorldUnloadedEventHandler {
    public static void onServerWorldUnloaded(MinecraftServer minecraftServer, ServerWorld serverWorld) {

        // Get the game managers
        var gameManager = serverWorld.getServerLasertagManager();

        // Dispose server
        gameManager.dispose();
    }
}

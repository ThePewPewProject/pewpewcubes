package de.kleiner3.lasertag.events.callback;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

/**
 * Event handler for the server world unloaded event
 *
 * @author Étienne Muser
 */
public class ServerWorldUnloadedEventHandler {
    public static void onServerWorldUnloaded(MinecraftServer minecraftServer, ServerWorld serverWorld) {
        // Reset game manager
        LasertagGameManager.reset();

        // Dispose server
        minecraftServer.getLasertagServerManager().dispose();
    }
}

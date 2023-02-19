package de.kleiner3.lasertag.events.callback;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

/**
 * Event handler for the player disconnected event
 *
 * @author Étienne Muser
 */
public class PlayerDisconnectEventHandler {
    public static void onPlayerDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        // Get the player
        var player = handler.getPlayer();

        // Remove player from his team
        server.playerLeaveHisTeam(player);
    }
}

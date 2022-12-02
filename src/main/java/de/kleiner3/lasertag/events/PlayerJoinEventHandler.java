package de.kleiner3.lasertag.events;

import de.kleiner3.lasertag.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.types.Colors;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

/**
 * Event handler for the player join event
 *
 * @author Étienne Muser
 */
public class PlayerJoinEventHandler {
    public static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender ignoredSender, MinecraftServer server) {
        LasertagSettingsManager.syncToPlayer(handler.getPlayer());
        Colors.syncTeamsToClient(handler.getPlayer());
        server.syncTeamsAndScoresToPlayer(handler.getPlayer());
    }
}

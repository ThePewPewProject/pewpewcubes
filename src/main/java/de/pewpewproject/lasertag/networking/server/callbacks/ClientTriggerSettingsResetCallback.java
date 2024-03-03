package de.pewpewproject.lasertag.networking.server.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Callback for the client trigger settings reset network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerSettingsResetCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game mangers
            var gameManager = server.getOverworld().getServerLasertagManager();
            var settingsManager = gameManager.getSettingsManager();

            settingsManager.reset();
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerSettingsResetCallback", ex);
            throw ex;
        }
    }
}

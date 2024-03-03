package de.pewpewproject.lasertag.networking.server.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Callback for the client trigger setting reset network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerSettingResetCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = server.getOverworld().getServerLasertagManager();
            var settingsManager = gameManager.getSettingsManager();

            // Get setting name
            var settingName = buf.readString();

            // Reset the setting
            settingsManager.reset(settingName);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerSettingResetCallback", ex);
            throw ex;
        }
    }
}

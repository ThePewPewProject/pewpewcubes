package de.pewpewproject.lasertag.networking.server.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Callback for the client trigger load settings preset network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerLoadPresetCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = server.getOverworld().getServerLasertagManager();
            var settingsPresetsManager = gameManager.getSettingsPresetsManager();

            // Get the preset name
            var presetName = buf.readString();

            settingsPresetsManager.loadPreset(presetName);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerLoadPresetCallback", ex);
            throw ex;
        }
    }
}

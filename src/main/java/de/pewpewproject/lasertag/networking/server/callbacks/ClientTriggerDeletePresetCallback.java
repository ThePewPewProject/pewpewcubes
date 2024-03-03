package de.pewpewproject.lasertag.networking.server.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Callback for the client trigger delete preset network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerDeletePresetCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManagers = server.getOverworld().getServerLasertagManager();
            var settingsPresetsManager = gameManagers.getSettingsPresetsManager();
            var settingsPresetsNameManager = gameManagers.getSettingsPresetsNameManager();

            // Get the preset name
            var presetName = buf.readString();

            settingsPresetsManager.deletePreset(presetName);
            settingsPresetsNameManager.removePresetName(presetName);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerDeletePresetCallback", ex);
            throw ex;
        }
    }
}

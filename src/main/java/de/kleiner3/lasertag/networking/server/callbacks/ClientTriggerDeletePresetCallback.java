package de.kleiner3.lasertag.networking.server.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
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

            // Get the preset name
            var presetName = buf.readString();

            server.getLasertagServerManager().getSettingsPresetsManager().deletePreset(presetName);
            LasertagGameManager.getInstance().getPresetsNameManager().removePresetName(server, presetName);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerDeletePresetCallback", ex);
            throw ex;
        }
    }
}

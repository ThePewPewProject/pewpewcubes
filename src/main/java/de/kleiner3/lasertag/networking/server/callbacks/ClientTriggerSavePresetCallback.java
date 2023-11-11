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
 * Callback for the client trigger save settings preset network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerSavePresetCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Read name from buffer
            var presetName = buf.readString();

            server.getLasertagServerManager().getSettingsPresetsManager().savePreset(presetName);
            LasertagGameManager.getInstance().getPresetsNameManager().addPresetName(server, presetName);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerSavePresetCallback", ex);
            throw ex;
        }
    }
}

package de.kleiner3.lasertag.networking.server.callbacks;

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

        // Get the preset name
        var presetName = buf.readString();

        server.getLasertagServerManager().getSettingsPresetsManager().loadPreset(presetName, server);
    }
}

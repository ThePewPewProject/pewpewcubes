package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.common.util.ConverterUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the lasertag settings changed network event
 *
 * @author Étienne Muser
 */
public class LasertagSettingsChangedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // Read from buffer
        var settingsName = buf.readString();
        var value = buf.readString();

        // Convert to primitive type
        var primitive = ConverterUtil.stringToPrimitiveType(value);

        LasertagGameManager.getInstance().getSettingsManager().set(null, settingsName, primitive);
    }
}

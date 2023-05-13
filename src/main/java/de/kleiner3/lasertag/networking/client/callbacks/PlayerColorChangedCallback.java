package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the player changed color network event
 *
 * @author Étienne Muser
 */
public class PlayerColorChangedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        // Read username from bufffer
        var playerUsername = buf.readString();

        // Read color from buffer
        var newColor = buf.readNullable(PacketByteBuf::readInt);

        // Put new color into player color manager
        LasertagGameManager.getInstance().getPlayerColorManager().put(playerUsername, newColor);
    }
}

package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the set lasertarget deactivation network event
 *
 * @author Étienne Muser
 */
public class SetLasertargetDeactivatedCallback implements ClientPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            client.world.getClientLasertagManager()
                    .getLasertargetsManager()
                    .setDeactivated(buf.readBlockPos(), buf.readBoolean());
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in SetLasertargetDeactivatedCallback", ex);
            throw ex;
        }
    }
}

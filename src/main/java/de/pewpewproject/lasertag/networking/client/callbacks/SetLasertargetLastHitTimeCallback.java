package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the set lasertarget last hit time network event
 *
 * @author Étienne Muser
 */
public class SetLasertargetLastHitTimeCallback implements ClientPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            client.world.getClientLasertagManager()
                    .getLasertargetsManager()
                    .setLastHitTime(buf.readBlockPos(), buf.readLong());
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in SetLasertargetLastHitTimeCallback", ex);
            throw ex;
        }
    }
}

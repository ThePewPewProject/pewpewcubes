package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;

/**
 * Callback for the player deactivated network sound event
 *
 * @author Étienne Muser
 */
public class PlayerDeactivatedSoundEventCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Execute sound playing on main thread to avoid weird exceptions
            client.execute(() ->
                    client.player.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1.0F, 1.0F));
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in PlayerDeactivatedSoundEventCallback", ex);
            throw ex;
        }
    }
}

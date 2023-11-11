package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;

/**
 * Callback for the player scored network sound event
 *
 * @author Étienne Muser
 */
public class PlayerScoredSoundEventCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Execute sound playing on main thread to avoid weird exceptions
            client.execute(() ->
                    client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F));
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in PlayerScoredSoundEventCallback", ex);
            throw ex;
        }
    }
}

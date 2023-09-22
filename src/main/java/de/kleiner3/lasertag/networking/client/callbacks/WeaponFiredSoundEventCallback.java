package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.client.SoundEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;

/**
 * Callback for the weapon fired network sound event
 *
 * @author Étienne Muser
 */
public class WeaponFiredSoundEventCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // Get position of sound event
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        // Execute sound playing on main thread to avoid weird exceptions
        client.execute(() ->
                client.world.playSound(x, y, z,
                        SoundEvents.LASERWEAPON_FIRE_SOUND_EVENT, SoundCategory.BLOCKS,
                        1.0F, 1.0F, true));
    }
}

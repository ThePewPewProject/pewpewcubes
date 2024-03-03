package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Callback to handle the error message recieved network event
 *
 * @author Étienne Muser
 */
public class ErrorMessageCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            client.player.sendMessage(Text.translatable(buf.readString())
                    .fillStyle(Style.EMPTY.withColor(Formatting.RED)), true);
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ErrorMessageCallback", ex);
            throw ex;
        }
    }
}

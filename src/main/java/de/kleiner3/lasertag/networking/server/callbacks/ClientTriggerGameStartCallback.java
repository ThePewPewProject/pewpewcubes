package de.kleiner3.lasertag.networking.server.callbacks;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Callback for the client trigger game start network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerGameStartCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            var abortReasons = server.getLasertagServerManager().startGame(false);

            abortReasons.ifPresent(feedback -> {
                server.getPlayerManager().broadcast(Text.literal("Start game aborted. Reasons:\n" + feedback).formatted(Formatting.RED), false);
            });
        });
    }
}

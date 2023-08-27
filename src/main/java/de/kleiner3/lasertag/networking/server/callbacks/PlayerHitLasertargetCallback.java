package de.kleiner3.lasertag.networking.server.callbacks;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Callback to handle when a client hit a lasertarget
 *
 * @author Étienne Muser
 */
public class PlayerHitLasertargetCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var playerUuid = buf.readUuid();

        var targetX = buf.readDouble();
        var targetY = buf.readDouble();
        var targetZ = buf.readDouble();

        var targetPos = new BlockPos(targetX, targetY, targetZ);

        server.getLasertagServerManager().playerHitLasertarget(playerUuid, targetPos);
    }
}

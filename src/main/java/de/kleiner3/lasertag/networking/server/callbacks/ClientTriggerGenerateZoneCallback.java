package de.kleiner3.lasertag.networking.server.callbacks;

import de.kleiner3.lasertag.block.entity.LasertagTeamZoneGeneratorBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Étienne Muser
 */
public class ClientTriggerGenerateZoneCallback implements ServerPlayNetworking.PlayChannelHandler{
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        // Get the block pos
        var blockPos = buf.readBlockPos();

        // Get the team name
        var newTeamName = buf.readString();

        server.execute(() -> {
            // Get the block entity
            var blockEntity = server.getOverworld().getBlockEntity(blockPos);

            // If it is not an team zone generator block entity
            if (!(blockEntity instanceof LasertagTeamZoneGeneratorBlockEntity lasertagTeamZoneGeneratorBlockEntity)) {
                return;
            }

            // Set the team name
            lasertagTeamZoneGeneratorBlockEntity.setTeamName(newTeamName);

            // Do the floodfill
            lasertagTeamZoneGeneratorBlockEntity.generateZone();
        });
    }
}

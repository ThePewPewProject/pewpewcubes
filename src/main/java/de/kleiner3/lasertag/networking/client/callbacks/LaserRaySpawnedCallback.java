package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.entity.LaserRayEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

/**
 * Callback to handle the laser ray spawned networking event
 *
 * @author Étienne Muser
 */
public class LaserRaySpawnedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        double endX = buf.readDouble();
        double endY = buf.readDouble();
        double endZ = buf.readDouble();

        Vec3d pos = new Vec3d(x, y, z);
        Vec3d endPos = new Vec3d(endX, endY, endZ);

        float yaw = buf.readFloat();
        float pitch = buf.readFloat();

        int entityId = buf.readInt();
        UUID uuid = buf.readUuid();

        int color = buf.readInt();

        client.execute(() -> {
            LaserRayEntity entity = new LaserRayEntity(client.world, pos, yaw, pitch, color, endPos);
            entity.setId(entityId);
            entity.setUuid(uuid);

            client.world.addEntity(entityId, entity);
        });
    }
}

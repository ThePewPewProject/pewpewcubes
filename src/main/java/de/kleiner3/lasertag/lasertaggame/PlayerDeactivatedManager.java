package de.kleiner3.lasertag.lasertaggame;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDeactivatedManager {
    private static HashMap<UUID, Boolean> deactivatedMap = new HashMap<>();


    /**
     * @return If the player is currently deactivated
     */
    public static boolean isDeactivated(UUID uuid) {
        // If not already in map
        if (deactivatedMap.containsKey(uuid) == false) {
            deactivatedMap.put(uuid, true);
        }

        return deactivatedMap.get(uuid);
    }

    /**
     * Sets if the player is deactivated
     * @param deactivated
     */
    public static void setDeactivated(UUID uuid, boolean deactivated) {
        deactivatedMap.put(uuid, deactivated);
    }

    public static void deactivate(PlayerEntity player, World world) {
        var uuid = player.getUuid();

        // Deactivate player
        deactivatedMap.put(uuid, true);
        player.onDeactivated();
        sendDeactivatedToClients(world, uuid, true);

        new Thread(() -> {
            try {
                Thread.sleep(1000 * LasertagConfig.getInstance().getDeactivateTime());
            } catch (InterruptedException e) {}

            // Reactivate player
            deactivatedMap.put(uuid, false);
            player.onActivated();
            sendDeactivatedToClients(world, uuid, false);
        }).start();
    }

    public static void activate(UUID uuid, World world) {
        deactivatedMap.put(uuid, false);
        sendDeactivatedToClients(world, uuid, false);
    }

    public static void deactivate(PlayerEntity player, World world, boolean forever) {
        if (forever == true) {
            var uuid = player.getUuid();
            deactivatedMap.put(uuid, true);
            sendDeactivatedToClients(world, uuid, true);
        } else {
            deactivate(player, world);
        }
    }

    private static void sendDeactivatedToClients(World world, UUID uuid, boolean deactivated) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

            buf.writeUuid(uuid);
            buf.writeBoolean(deactivated);

            ServerEventSending.sendToEveryone(serverWorld, NetworkingConstants.PLAYER_DEACTIVATED_STATUS_CHANGED, buf);
        }
    }
}

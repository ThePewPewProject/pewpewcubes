package de.kleiner3.lasertag.networking.server;

import de.kleiner3.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Helper class to send events from server to client
 *
 * @author Étienne Muser
 */
public class ServerEventSending {

    /**
     * Send an error message to the specified client
     *
     * @param client The ServerPlayerEntity to send the error message to
     * @param errorMessage The error message to send
     */
    public static void sendErrorMessageToClient(ServerPlayerEntity client, String errorMessage) {
        // Create packet buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Write errorMessage to buffer
        buf.writeString(errorMessage);

        // Send to all clients
        ServerPlayNetworking.send(client, NetworkingConstants.ERROR_MESSAGE, buf);
    }

    /**
     * Sends a PacketByteBuf to every player in the given world
     *
     * @param world The world where the players are in
     * @param id The identifier of the event to send
     * @param buf The data to send
     */
    public static void sendToEveryone(ServerWorld world, Identifier id, PacketByteBuf buf) {
        // Get all players
        List<ServerPlayerEntity> players = world.getPlayers();

        // For each player
        for (ServerPlayerEntity player : players) {
            ServerPlayNetworking.send(player, id, buf);
        }
    }

    /**
     * Sends the player scored sound event to the client
     *
     * @param client The ServerPlayerEntity to send the event to
     */
    public static void sendPlayerScoredSoundEvent(ServerPlayerEntity client) {
        ServerPlayNetworking.send(client, NetworkingConstants.PLAY_PLAYER_SCORED_SOUND, PacketByteBufs.empty());
    }
}

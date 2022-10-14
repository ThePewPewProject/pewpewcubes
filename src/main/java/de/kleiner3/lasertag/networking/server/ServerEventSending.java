package de.kleiner3.lasertag.networking.server;

import java.util.List;

import de.kleiner3.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/**
 * Helper class to send events from server to client
 * 
 * @author Étienne Muser
 *
 */
public class ServerEventSending {
	
	/**
	 * Send an error message to the specified client
	 * @param client
	 * @param errorMessage
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
	 * @param world
	 * @param id
	 * @param buf
	 */
	public static void sendToEveryone(ServerWorld world, Identifier id, PacketByteBuf buf) {
		// Get all players
		List<ServerPlayerEntity> players = world.getPlayers();
		
		// For each player
		for (ServerPlayerEntity player : players) {
			ServerPlayNetworking.send(player, id, buf);
		}
	}
}

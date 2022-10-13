package de.kleiner3.lasertag.networking.client;

import java.util.UUID;

import de.kleiner3.lasertag.entity.LaserRayEntity;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.types.Colors;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

/**
 * Class to handle all networking on the client
 * 
 * @author Étienne Muser
 *
 */
public class ClientNetworkingHandler {
	public ClientNetworkingHandler() {
		
	}
	
	/**
	 * Register everything
	 */
	public void register() {
		ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.LASER_RAY_SPAWNED, Callbacks::handleLaserRaySpawned);
	}
	
	/**
	 * Class containing all callbacks needed by the ClientNetworkingHandler
	 * 
	 * @author Étienne Muser
	 *
	 */
	private class Callbacks {
		public static void handleLaserRaySpawned(MinecraftClient client,
												 ClientPlayNetworkHandler handler,
												 PacketByteBuf buf,
												 PacketSender responseSender)
		{
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
		    
		    Colors color = Colors.valueOf(buf.readString());
		    
		    client.execute(() -> {
		    	LaserRayEntity entity = new LaserRayEntity(client.world, pos, yaw, pitch, color, endPos);
			    entity.setId(entityId);
			    entity.setUuid(uuid);
			    
			    client.world.addEntity(entityId, entity);
		    });
		}
	}
}

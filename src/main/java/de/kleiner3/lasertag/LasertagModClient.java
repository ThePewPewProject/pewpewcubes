package de.kleiner3.lasertag;

import java.util.UUID;

import de.kleiner3.lasertag.Types.Colors;
import de.kleiner3.lasertag.entity.LaserRayEntity;
import de.kleiner3.lasertag.entity.render.LaserRayEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.util.math.Vec3d;

/**
 * Initializes the client side of the mod
 * 
 * @author Étienne Muser
 *
 */
@Environment(EnvType.CLIENT)
public class LasertagModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// ===== Register entity renderers ====================
		EntityRendererRegistry.register(LasertagMod.LASER_RAY, (ctx) -> {
			return new LaserRayEntityRenderer(ctx);
		});
		
		// ===== Register packet recievers ====================
		// TODO: Make seperate classes to do network handling
		ClientPlayNetworking.registerGlobalReceiver(LaserRayEntity.LASER_RAY_SPAWNED, (client, handler, buf, responseSender) -> {
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
		});
	}

}

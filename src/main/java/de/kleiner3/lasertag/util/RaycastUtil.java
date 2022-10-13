package de.kleiner3.lasertag.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

/**
 * Utility class to do Raycasting
 * 
 * @author Étiennne Muser
 *
 */
public class RaycastUtil {
	/**
	 * Raycasts into the direction of the crosshair
	 * @return The HitResult of the raycast
	 */
	public static HitResult raycastCrosshair(PlayerEntity playerEntity, int maxDistance) {
		// Get the cameras position to start the raycasting from
		Vec3d startPos = playerEntity.getCameraPosVec(1.0f);
		
		// Get the cameras direction vector to raycast in that direction
        Vec3d direction = playerEntity.getRotationVec(1.0f);
        
        // Lengthen the direction vector to match maxDistance
        Vec3d ray = startPos.add(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance);
        
        // Do the raycast (ignore fluids) and return the result
        return playerEntity.world.raycast(new RaycastContext(startPos, ray, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, playerEntity));
	}
}

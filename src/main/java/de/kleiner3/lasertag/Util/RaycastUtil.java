package de.kleiner3.lasertag.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.RaycastContext;

public class RaycastUtil {
	/**
	 * Raycasts into the direction of the crosshair
	 * @return The HitResult of the raycast
	 */
	public static HitResult raycastCrosshair(PlayerEntity playerEntity, int maxDistance) {
		Vec3d vec3d = playerEntity.getCameraPosVec(1.0f);
        Vec3d vec3d2 = playerEntity.getRotationVec(1.0f);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        return playerEntity.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, playerEntity));
	}
}

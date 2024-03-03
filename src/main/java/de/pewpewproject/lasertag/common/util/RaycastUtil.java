package de.pewpewproject.lasertag.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

/**
 * Utility class to do Raycasting
 *
 * @author Étiennne Muser
 */
public class RaycastUtil {
    /**
     * Raycasts into the direction of the crosshair
     *
     * @return The HitResult of the raycast
     */
    public static HitResult raycastCrosshair(PlayerEntity playerEntity, int maxDistance) {

        // Get the cameras position to start the raycasting from
        Vec3d startPos = playerEntity.getCameraPosVec(1.0f);

        // Get the cameras direction vector to raycast in that direction
        Vec3d direction = playerEntity.getRotationVec(1.0f);

        // Lengthen the direction vector to match maxDistance
        Vec3d ray = startPos.add(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance);

        // Do the block-raycast (ignore fluids) and return the result
        BlockHitResult blockHit = playerEntity.world.raycast(new RaycastContext(startPos, ray, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, playerEntity));

        Box box = playerEntity
                .getBoundingBox()
                .stretch(playerEntity.getRotationVec(1.0F).multiply(maxDistance))
                .expand(1.0D, 1.0D, 1.0D);

        // Do the entity-raycast
        EntityHitResult entityhit = ProjectileUtil.raycast(playerEntity, startPos, ray, box, entity -> !entity.isSpectator(), Double.MAX_VALUE);

        // If no entity was hit
        if (entityhit == null) {
            return blockHit;
        }

        // If entity is closer than block
        if (startPos.squaredDistanceTo(entityhit.getPos()) < startPos.squaredDistanceTo(blockHit.getPos())) {
            return entityhit;
        }

        return blockHit;
    }
}

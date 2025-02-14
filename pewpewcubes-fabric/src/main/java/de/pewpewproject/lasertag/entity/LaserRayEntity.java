package de.pewpewproject.lasertag.entity;

import de.pewpewproject.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * An Entity to show the laser ray
 *
 * @author Étienne Muser
 */
public class LaserRayEntity extends Entity {

    /**
     * The color of the ray
     */
    private int color;
    /**
     * The end of the laser ray in world coordinates
     */
    private Vec3d end;

    /**
     * The start of the laser ray in world coordinates
     */
    private Vec3d start;

    public LaserRayEntity(EntityType<? extends LaserRayEntity> type, World world) {
        super(type, world);
    }

    public LaserRayEntity(World world, LivingEntity owner, int color, HitResult hit) {
        this(world,
                owner.getEyePos().add(new Vec3d(0.0, -0.2, 0.0)),
                owner.getYaw(),
                owner.getPitch(),
                color, hit.getPos());
    }

    public LaserRayEntity(World world, Vec3d startPos, float yaw, float pitch, int color, Vec3d endPos) {
        super(Entities.LASER_RAY, world);

        this.color = color;
        this.start = startPos;
        this.end = endPos;

        this.setYaw(yaw);
        this.setPitch(pitch);
        this.setPosition(startPos);

        float xWidth = (float) Math.abs(endPos.x - startPos.x);
        float zWidth = (float) Math.abs(endPos.z - startPos.z);
        float height = (float) Math.abs(endPos.y - startPos.y);
        this.dimensions = EntityDimensions.changing(Math.max(xWidth, zWidth), height);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        // Overwrite entity dimensions
        return this.dimensions;
    }

    @Override
    protected Box calculateBoundingBox() {
        // If start or end pos are not set yet
        if (this.start == null || this.end == null) {
            // Bounding box cannot be calculated
            return null;
        }

        // Create bounding box to go from laser ray start to end
        return new Box(this.start, this.end);
    }

    public int getColor() {
        return this.color;
    }

    /**
     * Gets the end position of the ray in world coordinates
     *
     * @return the position vector of the end position
     */
    public Vec3d getEnd() {
        return end;
    }

    /**
     * Gets the start position of the ray in world coordinates
     *
     * @return the position vector of the start position
     */
    public Vec3d getStart() { return start; }

    @Override
    protected void initDataTracker() {
        // Empty
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    /**
     * Send the spawn information to the client.
     * This manual step is necessary, because for some reason only living entities have this done automatically
     */
    @Override
    public Packet<?> createSpawnPacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Put position
        buf.writeDouble(getX());
        buf.writeDouble(getY());
        buf.writeDouble(getZ());

        // Put end position
        buf.writeDouble(end.x);
        buf.writeDouble(end.y);
        buf.writeDouble(end.z);

        // Put yaw & pitch
        buf.writeFloat(getYaw());
        buf.writeFloat(getPitch());

        // Put id
        buf.writeInt(getId());
        buf.writeUuid(getUuid());

        // Put color
        buf.writeInt(color);

        return ServerPlayNetworking.createS2CPacket(NetworkingConstants.LASER_RAY_SPAWNED, buf);
    }
}

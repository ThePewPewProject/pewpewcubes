package de.kleiner3.lasertag.entity;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * An Entity to show the laser ray
 *
 * @author Étienne Muser
 */
public class LaserRayEntity extends ProjectileEntity {

    /**
     * The color of the ray
     */
    private final int color;
    /**
     * The end of the laser ray in world coordinates
     */
    private Vec3d end;

    public LaserRayEntity(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);

        // Set default color Teal
        color = 0xFFFFFFFF;
        System.out.println("WRONG CONSTRUCTOR CALLED!!!");
    }

    public LaserRayEntity(World world, LivingEntity owner, int color, HitResult hit) {
        this(world, owner.getEyePos(), owner.getYaw(), owner.getPitch(), color, hit.getPos());
    }

    public LaserRayEntity(World world, Vec3d pos, float yaw, float pitch, int color, Vec3d endPos) {
        super(LasertagMod.LASER_RAY, world);

        this.color = color;
        this.end = endPos;
        this.setYaw(yaw);
        this.setPitch(pitch);
        this.setPosition(pos);
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

    @Override
    protected void initDataTracker() {

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

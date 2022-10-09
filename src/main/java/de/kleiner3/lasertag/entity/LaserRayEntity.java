package de.kleiner3.lasertag.entity;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.Types.Colors;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LaserRayEntity extends ProjectileEntity {

	public static final Identifier LASER_RAY_SPAWNED = new Identifier("laser_ray_spawned");
	
	private Colors color;
	private Vec3d end;

	public LaserRayEntity(EntityType<? extends ProjectileEntity> type, World world) {
		super(type, world);

		// Set default color Teal
		color = Colors.TEAL;
		System.out.println("WRONG CONSTRUCTOR CALLED!!!");
	}

	public LaserRayEntity(World world, LivingEntity owner, Colors color, HitResult hit) {
		this(world, owner.getEyePos(), owner.getYaw(), owner.getPitch(), color, hit.getPos());
	}

	public LaserRayEntity(World world, Vec3d pos, float yaw, float pitch, Colors color, Vec3d endPos) {
		super(LasertagMod.LASER_RAY, world);

		this.color = color;
		this.end = endPos;
		this.setPosition(pos);
	}

	public Colors getColor() {
		return color;
	}

	public Vec3d getEnd() {
		return end;
	}

	@Override
	protected void initDataTracker() {

	}
	
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
		buf.writeString(color.name());
		
		return ServerPlayNetworking.createS2CPacket(LASER_RAY_SPAWNED, buf);
	}
}

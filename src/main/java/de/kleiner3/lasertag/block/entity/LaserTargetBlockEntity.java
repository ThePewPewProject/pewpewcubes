package de.kleiner3.lasertag.block.entity;

import de.kleiner3.lasertag.lasertaggame.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.entity.Entities;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.lasertaggame.settings.SettingNames;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LaserTargetBlockEntity extends BlockEntity {

    /**
     * Represents the uuids of the players who hit the target already.
     */
    private List<UUID> hitBy = new LinkedList<>();
    private boolean deactivated = false;

    public LaserTargetBlockEntity(BlockPos pos, BlockState state) {
        super(Entities.LASER_TARGET_ENTITY, pos, state);
    }

    public void onHitBy(MinecraftServer server, PlayerEntity playerEntity) {
        // Check that target is activated
        if (deactivated) {
            return;
        }

        // Check that player didn't hit the target before
        if (alreadyHit(playerEntity)) {
            return;
        }

        server.onPlayerScored(playerEntity, (int)(long)LasertagSettingsManager.get(SettingNames.LASERTARGET_HIT_SCORE));
        ServerEventSending.sendPlayerScoredSoundEvent((ServerPlayerEntity) playerEntity);

        // Register on server
        server.registerLasertarget(this);

        // Deactivate
        deactivated = true;

        // Reactivate after configured amount of seconds
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            deactivated = false;
        }, (long)LasertagSettingsManager.get(SettingNames.LASERTARGET_DEACTIVATE_TIME), TimeUnit.SECONDS);

        // Add player to the players who hit the target
        hitBy.add(playerEntity.getUuid());
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("deactivated", deactivated);

        var hitByFlattened = new LinkedList<Long>();
        for (var uuid : hitBy) {
            hitByFlattened.add(uuid.getMostSignificantBits());
            hitByFlattened.add(uuid.getLeastSignificantBits());
        }
        nbt.putLongArray("hitBy", hitByFlattened);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        var hitByFlattened = nbt.getLongArray("hitBy");
        hitBy = new ArrayList<>();
        for(int i = 0; i < hitByFlattened.length; i += 2) {
            hitBy.add(new UUID(hitByFlattened[i], hitByFlattened[i+1]));
        }

        deactivated = nbt.getBoolean("deactivated");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void reset() {
        deactivated = false;
        hitBy = new LinkedList<>();
    }

    private boolean alreadyHit(PlayerEntity p) {
        var uuid = p.getUuid();

        for (var playerUuid : hitBy) {
            if (playerUuid.equals(uuid)) {
                return true;
            }
        }

        return false;
    }
}

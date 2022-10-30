package de.kleiner3.lasertag.block.entity;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.util.Tuple;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LaserTargetBlockEntity extends BlockEntity {

    /**
     * Represents the uuids of the players who hit the target already.
     * x := uuid.LSB
     * y := uuid.MSB
     */
    private List<Tuple<Long, Long>> hitBy = new ArrayList<>();
    private boolean deactivated = false;

    public LaserTargetBlockEntity(BlockPos pos, BlockState state) {
        super(LasertagMod.LASER_TARGET_ENTITY, pos, state);
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

        server.onPlayerScored(playerEntity, LasertagConfig.getInstance().getLasertargetHitScore());
        ServerEventSending.sendPlayerScoredSoundEvent((ServerPlayerEntity) playerEntity);

        // Register on server
        server.registerLasertarget((LaserTargetBlockEntity)(Object)this);

        // Deactivate
        deactivated = true;
        new Thread(() -> {
            try {
                Thread.sleep(LasertagConfig.getInstance().getLasertargetDeactivatedTime() * 1000);
            } catch (InterruptedException e) {}

            deactivated = false;
        }).start();

        // Add player to the players who hit the target
        var uuid = playerEntity.getUuid();
        hitBy.add(new Tuple<>(uuid.getLeastSignificantBits(), uuid.getMostSignificantBits()));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("deactivated", deactivated);

        var hitByFlattened = new LinkedList<Long>();
        for (var uuid : hitBy) {
            hitByFlattened.add(uuid.x);
            hitByFlattened.add(uuid.y);
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
            hitBy.add(new Tuple(hitByFlattened[i], hitByFlattened[i+1]));
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
        hitBy = new ArrayList<>();
    }

    private boolean alreadyHit(PlayerEntity p) {
        var uuid = p.getUuid();

        for (int i = 0; i < hitBy.size(); ++i) {
            if (hitBy.get(i).equals(new Tuple<>(uuid.getLeastSignificantBits(), uuid.getMostSignificantBits()))) {
                return true;
            }
        }

        return false;
    }

    // TODO: Reset already hit by after game stop
}

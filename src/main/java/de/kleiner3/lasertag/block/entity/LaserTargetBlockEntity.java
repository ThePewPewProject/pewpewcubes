package de.kleiner3.lasertag.block.entity;

import de.kleiner3.lasertag.entity.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class LaserTargetBlockEntity extends BlockEntity implements IAnimatable {

    private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

    /**
     * Represents the uuids of the players who hit the target already.
     */
    private List<UUID> hitBy = new LinkedList<>();
    private boolean deactivated = false;

    public LaserTargetBlockEntity(BlockPos pos, BlockState state) {
        super(Entities.LASER_TARGET_ENTITY, pos, state);
    }

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

    public boolean alreadyHitBy(PlayerEntity p) {
        var uuid = p.getUuid();

        for (var playerUuid : hitBy) {
            if (playerUuid.equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    public void addHitBy(PlayerEntity p) {
        hitBy.add(p.getUuid());
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    // Predicate runs every frame
    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    // All you need to do here is add your animation controllers to the
    // AnimationData
    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 16, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.animationFactory;
    }
}

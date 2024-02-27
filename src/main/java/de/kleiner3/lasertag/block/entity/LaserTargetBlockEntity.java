package de.kleiner3.lasertag.block.entity;

import de.kleiner3.lasertag.entity.Entities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LaserTargetBlockEntity extends BlockEntity implements IAnimatable {

    private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

    public LaserTargetBlockEntity(BlockPos pos, BlockState state) {
        super(Entities.LASER_TARGET_ENTITY, pos, state);
    }

    public long getTimeSinceLastHit() {

        // Get the game managers
        var clientManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var lasertargetsManager = clientManager.getLasertargetsManager();

        return Math.max(0, this.world.getTime() - lasertargetsManager.getLastHitTime(this.pos));
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // Predicate runs every frame
    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    // All you need to do here is add your animation controllers to the AnimationData
    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 16, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.animationFactory;
    }
}

package de.pewpewproject.lasertag.block.entity;

import de.pewpewproject.lasertag.block.LasertagFlagBlock;
import de.pewpewproject.lasertag.entity.Entities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * Block entity for the lasertag flag
 *
 * @author Étienne Muser
 */
public class LasertagFlagBlockEntity extends BlockEntity implements IAnimatable {

    private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

    public static final String TEAM_NAME_NBT_KEY = "teamName";

    private String teamName = "";
    private final DoubleBlockHalf half;

    public LasertagFlagBlockEntity(BlockPos pos, BlockState state) {
        super(Entities.LASERTAG_FLAG_BLOCK_ENTITY, pos, state);

        this.half = state.get(LasertagFlagBlock.HALF);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {

        nbt.putString(TEAM_NAME_NBT_KEY, this.teamName);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.teamName = nbt.getString(TEAM_NAME_NBT_KEY);
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

    public String getTeamName() {
        return this.teamName;
    }

    public void setTeamName(String newTeamName) {
        this.teamName = newTeamName;
    }

    public DoubleBlockHalf getHalf() {
        return this.half;
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 16, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.animationFactory;
    }
}

package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * Mixin into the RespawnAnchorBlock.class to disable the block when it is in an arena world
 *
 * @author Étienne Muser
 */
@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {

    @Inject(method = "onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {

        // If world is not server
        if (world.isClient) {
            return;
        }

        // If is not an arena world
        if (!(Objects.requireNonNull(Objects.requireNonNull(world.getServer()).getSaveProperties().getGeneratorOptions().getDimensions().get(DimensionOptions.OVERWORLD))
                .chunkGenerator instanceof ArenaChunkGenerator)) {
            return;
        }

        cir.setReturnValue(ActionResult.PASS);
    }
}

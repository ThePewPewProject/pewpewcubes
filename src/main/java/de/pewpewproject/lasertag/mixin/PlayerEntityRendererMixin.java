package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.item.Items;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the PlayerEntityRenderer.class to implement the holding pose for the lasertag weapon
 *
 * @author Étienne Muser
 */
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @Inject(method = "getArmPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;", at = @At("RETURN"), cancellable = true)
    private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var activationManager = gameManager.getActivationManager();

        var itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.LASERTAG_WEAPON) && !activationManager.isDeactivated(player.getUuid())) {
            cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
        }
    }
}

package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.item.Items;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the slot class to make lasertag vests effectively have curse of binding. The player can't take it off.
 *
 * @author Étienne Muser
 */
@Mixin(Slot.class)
public abstract class SlotMixin {

    @Inject(method = "canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void onCanTakeItems(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> cir) {

        // Get the item stack of the slot
        var itemStack = ((Slot)(Object)this).getStack();

        // Player can always take it of if he is in creative mode. But otherwise can't take of the lasertag vest.
        cir.setReturnValue(playerEntity.isCreative() || !itemStack.isOf(Items.LASERTAG_VEST));
    }
}

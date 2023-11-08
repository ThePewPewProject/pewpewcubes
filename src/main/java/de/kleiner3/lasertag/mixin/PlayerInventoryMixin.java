package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.item.Items;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the PlayerInventory.class to prevent the player from dropping his weapon.
 *
 * @author Étienne Muser
 */
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Inject(method = "dropSelectedItem(Z)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        // Get the main hand stack
        var mainHandStack = ((PlayerInventory)(Object)this).getMainHandStack();

        // If is lasertag weapon
        if (mainHandStack.isOf(Items.LASERTAG_WEAPON) ||
            mainHandStack.isOf(Items.LASERTAG_FLAG)) {

            // Don't drop it
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}

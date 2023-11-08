package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the ItemStack.class to stop the player from placing the flag
 *
 * @author Étienne Muser
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /**
     * Inject into the useOnBlock() which is called when the player tries to place an item stack
     *
     * @param context
     * @param cir
     */
    @Inject(method = "useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    private void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {

        // If this stack is of lasertag flag
        if (((ItemStack)(Object)this).isOf(Items.LASERTAG_FLAG)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}

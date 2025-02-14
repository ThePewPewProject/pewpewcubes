package de.pewpewproject.lasertag.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the LivingEntity.class to disable the knockback of the laserweapons
 *
 * @author Étienne Muser
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("RETURN"))
    private void deactivateLaserKnockback(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        // If the damage source is not a laser
        if (!source.name.equals("laser")) {
            return;
        }

        ((LivingEntity)(Object)this).knockbackVelocity = 0.0F;
        ((LivingEntity)(Object)this).velocityModified = false;
    }
}

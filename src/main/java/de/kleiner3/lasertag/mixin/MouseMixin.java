package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.client.KeyBindings;
import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin into the Mouse.class
 *
 * @author Étienne Muser
 */
@Mixin(Mouse.class)
public abstract class MouseMixin {

    /**
     * Slows down the mouse if the weapon is being zoomed
     *
     * @param instance
     * @return
     */
    @Redirect(method = "updateMouse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
    private Object zoom(SimpleOption<?> instance) {

        // Get the value from the instance
        var value = instance.getValue();

        // If this is the mouse sensitivity, then cast to double
        // Mouse.updateMouse has two calls to SimpleOption.getValue
        // one with a boolean value and the mouse sensitivity test
        // for Double. If it is a Double then this must be the mouse
        // sensitivity.
        if (value instanceof Double mouseSensitivity) {

            // Get the minecraft client
            var client = MinecraftClient.getInstance();

            // Get if the player is holding a lasertag weapon
            var playerIsHoldingWeapon = client.player.isHolding(Items.LASERTAG_WEAPON);

            // If the player is holding a weapon
            if (KeyBindings.isWeaponZoomPressed() && playerIsHoldingWeapon) {

                // Get the weapon zoom setting
                var zoomFactor = client.world.getClientLasertagManager().getSettingsManager().<Long>get(SettingDescription.WEAPON_ZOOM);

                // Calculate the new mouse sensitivity
                mouseSensitivity /= zoomFactor;
            }

            return mouseSensitivity;
        }

        // If it was not the mouse sensitivity, then simply return the value
        return value;
    }
}

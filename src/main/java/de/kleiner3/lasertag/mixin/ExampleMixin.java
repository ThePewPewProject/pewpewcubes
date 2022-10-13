package de.kleiner3.lasertag.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.TitleScreen;

/**
 * This is an example mixin into the TitleScreen class
 * 
 * @author Étienne Muser
 *
 */
@Mixin(TitleScreen.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		//LasertagMod.LOGGER.info("This line is printed by an example mod mixin!");
	}
}

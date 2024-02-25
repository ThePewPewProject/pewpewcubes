package de.kleiner3.lasertag.mixin.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into the CreateWorldScreen.class to set the cheats enabled by default.
 *
 * @author Étienne Muser
 */
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {

    /**
     * Overwrites the create world default values.
     * Sets cheats enabled to true, new world name to "New Arena World" and difficulty to peaceful.
     */
    @Inject(method = "<init>(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/resource/DataPackSettings;Lnet/minecraft/client/gui/screen/world/MoreOptionsDialog;)V", at = @At("TAIL"))
    private void setArenaDefaults(Screen parent, DataPackSettings dataPackSettings, MoreOptionsDialog moreOptionsDialog, CallbackInfo ci) {

        // Get this create world screen
        var createWorldScreen = ((CreateWorldScreen)(Object)this);

        // Overwrite the default values
        createWorldScreen.cheatsEnabled = true;
        createWorldScreen.tweakedCheats = true;
        createWorldScreen.levelName = I18n.translate("selectWorld.newArena");
        createWorldScreen.currentDifficulty = Difficulty.PEACEFUL;
    }
}

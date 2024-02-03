package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin into the DeathScreen.class to implement the respawn penalty if a game is running
 *
 * @author Étienne Muser
 */
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin {

    @Shadow
    private int ticksSinceDeath;

    @Final
    @Shadow
    private List<ButtonWidget> buttons;

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var uiState = gameManager.getSyncedState().getUIState();
        var settingsManager = gameManager.getSettingsManager();

        // Get the respawn cooldown
        long respawnCooldownSeconds = 1;

        // If a game is currently running
        if (uiState.isGameRunning) {

            // Use the setting
            respawnCooldownSeconds = settingsManager.get(SettingDescription.RESPAWN_PENALTY);
        }

        ++this.ticksSinceDeath;
        ButtonWidget buttonWidget;

        // 20 Ticks in a second
        if (this.ticksSinceDeath == respawnCooldownSeconds * 20) {
            for(var var1 = this.buttons.iterator(); var1.hasNext(); buttonWidget.active = true) {
                buttonWidget = var1.next();
            }
        }

        ci.cancel();
    }
}

package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.settings.valuetypes.CTFFlagHoldingPlayerVisibility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into the LivingEntityRenderer class
 *
 * @author Étienne Muser
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    /**
     * Inject into the hasLabel method to disable displaying name tags during a lasertag game
     * @param cir The CallbackInfoReturnable
     */
    @Inject(method = "hasLabel", at = @At("HEAD"), cancellable = true)
    private void onHasLabel(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {

        // Show nametag if the rendered entity is not a player
        if (!(livingEntity instanceof PlayerEntity renderedPlayer)) {
            return;
        }

        // Get managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var teamManager = gameManager.getTeamsManager();
        var captureTheFlagManager = gameManager.getCaptureTheFlagManager();
        var uiState = gameManager.getSyncedState().getUIState();
        var settingsManager = gameManager.getSettingsManager();

        // Get team of this player
        var thisPlayer = MinecraftClient.getInstance().player;
        var playersTeam = teamManager.getTeamOfPlayer(thisPlayer.getUuid());

        // If setting "show nametags of teammates" is set to true
        if (settingsManager.<Boolean>get(SettingDescription.SHOW_NAMETAGS_OF_TEAMMATES)) {

            var renderedPlayersTeam = teamManager.getTeamOfPlayer(renderedPlayer.getUuid());

            if (renderedPlayersTeam.equals(playersTeam)) {
                return;
            }
        }

        // If capture the flag setting "flagHoldingPlayerVisibility" is set to NAMETAG
        if (settingsManager.getEnum(SettingDescription.CTF_FLAG_HOLDING_PLAYER_VISIBILITY) == CTFFlagHoldingPlayerVisibility.NAMETAG) {

            // Get the team of the flag the player is holding
            var teamOptional = captureTheFlagManager.getPlayerHoldingFlagTeam(renderedPlayer.getUuid());

            // If player is holding a flag
            if (teamOptional.isPresent()) {
                return;
            }
        }

        if (!uiState.isGameRunning) {
            return;
        }

        // Always render name tags if player is in spectator game mode
        if (thisPlayer.isSpectator()) {
            return;
        }

        // Do not render nametag
        cir.setReturnValue(false);
        cir.cancel();
    }
}

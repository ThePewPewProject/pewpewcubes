package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicBoolean;

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
        var gameManager = LasertagGameManager.getInstance();
        var teamManager = gameManager.getTeamManager();
        var hudManager = gameManager.getHudRenderManager();
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

        if (hudManager.shouldRenderNameTags) {
            return;
        }

        AtomicBoolean isSpectator = new AtomicBoolean(false);
        playersTeam.ifPresent(t -> isSpectator.set(t.equals(TeamConfigManager.SPECTATORS)));
        if (isSpectator.get()) {
            return;
        }

        // Do not render nametag
        cir.setReturnValue(false);
        cir.cancel();
    }
}

package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
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
    private void onHasLabel(CallbackInfoReturnable<Boolean> cir) {

        var thisPlayer = MinecraftClient.getInstance().player;
        var playersTeam = LasertagGameManager.getInstance().getTeamManager().getTeamOfPlayer(thisPlayer.getUuid());
        AtomicBoolean isSpectator = new AtomicBoolean(false);
        playersTeam.ifPresent(t -> isSpectator.set(t.equals(TeamConfigManager.SPECTATORS)));

        if (!LasertagGameManager.getInstance().getHudRenderManager().shouldRenderNameTags && !isSpectator.get()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}

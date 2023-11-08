package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin into the WorldRenderer.class to colorize the glow of players
 *
 * @author Étienne Muser
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I"))
    private int onGetTeamColorValueInRender(Entity instance) {

        // Get the manager
        var flagManager = LasertagGameManager.getInstance().getFlagManager();

        // Get the team of the flag the player is holding
        var teamOptional = flagManager.getPlayerHoldingFlagTeam(instance.getUuid());

        // Return the color of the flags team or players default minecraft team color
        return teamOptional
                .map(team -> team.color().getValue())
                .orElse(instance.getTeamColorValue());
    }
}

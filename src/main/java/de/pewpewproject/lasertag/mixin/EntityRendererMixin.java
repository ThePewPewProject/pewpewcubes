package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.settings.valuetypes.CTFFlagHoldingPlayerVisibility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into the EntityRenderer.class to implement the colored nametags
 *
 * @author Étienne Muser
 */
@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @ModifyVariable(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int renderInFullBrightness(int light) {
        return 255;
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"))
    void onDrawTextInLabel(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

        // If rendered entity is not a player
        if (!(entity instanceof PlayerEntity player)) {
            return;
        }

        // Get the managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var settingsManager = gameManager.getSettingsManager();
        var teamManager = gameManager.getTeamsManager();
        var teamsConfigState = gameManager.getSyncedState().getTeamsConfigState();
        var captureTheFlagManager = gameManager.getCaptureTheFlagManager();

        // If capture the flag setting "flagHoldingPlayerVisibility" is set to NAMETAG
        if (settingsManager.getEnum(SettingDescription.CTF_FLAG_HOLDING_PLAYER_VISIBILITY) == CTFFlagHoldingPlayerVisibility.NAMETAG) {

            // Get the team of the flag the rendered player is holding
            var teamOptional = captureTheFlagManager.getPlayerHoldingFlagTeam(player.getUuid());

            // If player is holding a flag
            if (teamOptional.isPresent()) {

                // Get the color of the team
                var playerColor = teamOptional.get().color().getValue();

                // Set the text color of the name tag
                var mutableText = (MutableText)text;
                mutableText.setStyle(Style.EMPTY.withColor(playerColor));

                return;
            }
        }

        teamManager.getTeamOfPlayer(player.getUuid())
                .ifPresent(playerTeamId -> {

                    var playerTeam = teamsConfigState.getTeamOfId(playerTeamId).orElseThrow();

                    var playerColor = playerTeam.color().getValue();

                    var mutableText = (MutableText)text;
                    mutableText.setStyle(Style.EMPTY.withColor(playerColor));
                });
    }
}

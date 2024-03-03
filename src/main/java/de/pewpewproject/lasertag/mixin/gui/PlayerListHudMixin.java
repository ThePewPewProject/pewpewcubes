package de.pewpewproject.lasertag.mixin.gui;

import de.pewpewproject.lasertag.client.hud.TeamListHudOverlay;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into the PlayerListHud.class to implement the custom lasertag team list
 *
 * @author Étienne Muser
 */
@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    private final TeamListHudOverlay teamListHudOverlay = new TeamListHudOverlay();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrices, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {

        // Call own render method
        teamListHudOverlay.render(matrices);

        // Prevent the rest of the render code of PlayerListHud from executing
        ci.cancel();
    }
}

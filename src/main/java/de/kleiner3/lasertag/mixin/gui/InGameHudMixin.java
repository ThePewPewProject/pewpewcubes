package de.kleiner3.lasertag.mixin.gui;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin to show team list even tho the world is in singleplayer
 *
 * @author Étienne Muser
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

    /**
     * Hack to show team list even if world is in singleplayer.
     * Set scoreboardObjective2 to non-null value so that second hand of OR-operator is always false. Therefore
     * Only !this.client.options.playerListKey.isPressed() matters
     *
     * @param original
     * @return
     */
    @ModifyVariable(method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"), name = "scoreboardObjective2")
    private ScoreboardObjective modifyScoreboardObjective2(ScoreboardObjective original) {
        return new ScoreboardObjective(null, null, null, Text.literal("<dummy>"), null);
    }
}

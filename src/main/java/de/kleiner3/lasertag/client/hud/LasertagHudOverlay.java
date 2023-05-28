package de.kleiner3.lasertag.client.hud;

import de.kleiner3.lasertag.common.util.DurationUtils;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gui.LasertagHudRenderManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.time.Duration;

/**
 * Class to implement the custom overlay for the lasertag game
 *
 * @author Étienne Muser
 */
public class LasertagHudOverlay implements HudRenderCallback {
    //region Rendering

    /**
     * Render the lasertag HUD overlay
     *
     * @param matrixStack the matrixStack
     * @param tickDelta   Progress for linearly interpolating between the previous and current game state
     */
    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {

        // Get the client
        MinecraftClient client = MinecraftClient.getInstance();

        // If we are on the server
        if (client == null) {
            return;
        }

        // Get the render data
        var renderData = LasertagGameManager.getInstance().getHudRenderManager();

        // Calculate window size
        renderData.width = client.getWindow().getScaledWidth();
        renderData.wMid = renderData.width / 2;
        renderData.height = client.getWindow().getScaledHeight();
        renderData.hMid = renderData.height / 2;

        // Get the clients text renderer
        TextRenderer renderer = client.textRenderer;

        // Render HUD
        renderTimer(renderer, matrixStack, renderData);
        renderProgressBar(renderer, matrixStack, renderData);
        renderStartingIn(renderer, matrixStack, renderData);
    }

    private void renderStartingIn(TextRenderer renderer, MatrixStack matrices, LasertagHudRenderManager renderData) {
        // If starting in is -1 (the halting value)
        if (renderData.startingIn <= -1) {
            // Abort
            return;
        }

        if (renderData.startingIn == 0) {
            DrawableHelper.drawCenteredText(matrices, renderer, "GO!", renderData.wMid, renderData.hMid, 0xFFFFFFFF);
        } else {
            DrawableHelper.drawCenteredText(matrices, renderer, "Starting in " + renderData.startingIn, renderData.wMid, renderData.hMid, 0xFFFFFFFF);
        }
    }

    private void renderProgressBar(TextRenderer renderer, MatrixStack matrices, LasertagHudRenderManager renderData) {
        // If progress is 0
        if (renderData.progress == 0.0) {
            // Abort
            return;
        }

        int barStart = renderData.wMid - (LasertagHudRenderManager.progressBarWidth / 2);
        int progressWidth = (int) (LasertagHudRenderManager.progressBarWidth * renderData.progress);

        // Draw background
        DrawableHelper.fill(matrices, barStart, renderData.hMid + 10, barStart + LasertagHudRenderManager.progressBarWidth, renderData.hMid + 15, LasertagHudRenderManager.boxColor);

        // Draw progress
        DrawableHelper.fill(matrices, barStart, renderData.hMid + 10, barStart + progressWidth, renderData.hMid + 15, 0xFFFFFFFF);

        // Draw text
        DrawableHelper.drawCenteredText(matrices, renderer, "Scanning for spawn points...", renderData.wMid, renderData.hMid - 15, 0xFFFFFFFF);
    }

    private void renderTimer(TextRenderer renderer, MatrixStack matrices, LasertagHudRenderManager renderData) {
        // If game time should not be rendered
        if (!LasertagGameManager.getInstance().getSettingsManager().<Boolean>get(SettingDescription.RENDER_TIMER)) {
            // Abort
            return;
        }

        DrawableHelper.drawCenteredText(matrices, renderer,
                DurationUtils.toString(
                        Duration.ofSeconds((LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PLAY_TIME) * 60L) - renderData.gameTime)),
                renderData.wMid, LasertagHudRenderManager.textPadding, 0xFFFFFF);
    }

    //endregion
}

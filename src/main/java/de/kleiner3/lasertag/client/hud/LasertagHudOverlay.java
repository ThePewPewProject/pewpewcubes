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
        renderGameOver(renderer, matrixStack, renderData);
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

        // Get the managers
        var gameManager = LasertagGameManager.getInstance();
        var settingsManager = gameManager.getSettingsManager();

        // If game time should not be rendered
        if (!settingsManager.<Boolean>get(SettingDescription.RENDER_TIMER)) {
            // Abort
            return;
        }

        // Get the game mode
        var gameMode = gameManager.getGameModeManager().getGameMode();

        // Init seconds to show with the time the game is already running
        var secondsToShow = renderData.gameTime;

        // If game mode has no infinite time
        if (!gameMode.hasInfiniteTime()) {
            secondsToShow = (settingsManager.<Long>get(SettingDescription.PLAY_TIME) * 60L) - secondsToShow;
        }

        DrawableHelper.drawCenteredText(matrices, renderer,
                DurationUtils.toString(Duration.ofSeconds(secondsToShow)),
                renderData.wMid, LasertagHudRenderManager.textPadding, 0xFFFFFF);
    }

    private void renderGameOver(TextRenderer renderer, MatrixStack matrices, LasertagHudRenderManager renderData) {

        var winnerTeamId = renderData.lastGameWinnerId;

        if (winnerTeamId == -1) {
            return;
        }

        var winnerTeamOptional = LasertagGameManager.getInstance().getTeamManager().getTeamConfigManager().getTeamOfId(winnerTeamId);

        winnerTeamOptional.ifPresent(winnerTeam -> {

            matrices.push();

            var textColor = 0xFFFFFFFF;

            matrices.scale(2, 2, 2);
            DrawableHelper.drawCenteredText(matrices, renderer, "GAME OVER", renderData.wMid / 2, renderData.hMid / 2 - 15, textColor);

            matrices.scale(0.5F, 0.5F, 0.5F);
            var beginningString = "Team ";
            var endString = " won!";
            var beginningAndNameString = beginningString + winnerTeam.name();
            var wholeString = beginningAndNameString + endString;
            var widthOfBeginningString = renderer.getWidth(beginningString);
            var widthOfBeginningAndName = renderer.getWidth(beginningAndNameString);
            var widthOfWholeString = renderer.getWidth(wholeString);
            var textStartX = renderData.wMid - (widthOfWholeString / 2);
            var textY = renderData.hMid + 10;

            renderer.draw(matrices, beginningString, textStartX, textY, textColor);
            renderer.draw(matrices, winnerTeam.name(), textStartX + widthOfBeginningString, textY, winnerTeam.color().getValue());
            renderer.draw(matrices, endString, textStartX + widthOfBeginningAndName, textY, textColor);

            matrices.pop();
        });
    }

    //endregion
}

package de.pewpewproject.lasertag.client.hud;

import de.pewpewproject.lasertag.common.util.DurationUtils;
import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.IGameModeManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.client.ISettingsManager;
import de.pewpewproject.lasertag.lasertaggame.state.synced.ITeamsConfigState;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.UIState;
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

        // Get the client managers
        var clientManager = client.world.getClientLasertagManager();
        var settingsManager = clientManager.getSettingsManager();
        var gameModeManager = clientManager.getGameModeManager();
        var syncedState = clientManager.getSyncedState();
        var teamConfigState = syncedState.getTeamsConfigState();

        // Get the render data
        var uiState = clientManager.getSyncedState().getUIState();

        // Calculate window size
        var width = client.getWindow().getScaledWidth();
        var wMid = width / 2;
        var height = client.getWindow().getScaledHeight();
        var hMid = height / 2;

        // Get the clients text renderer
        TextRenderer renderer = client.textRenderer;

        // Render HUD
        renderTimer(renderer, matrixStack, uiState, settingsManager, gameModeManager, wMid);
        renderProgressBar(renderer, matrixStack, uiState, wMid, hMid);
        renderStartingIn(renderer, matrixStack, uiState, wMid, hMid);
        renderGameOver(renderer, matrixStack, uiState, teamConfigState, wMid, hMid);
    }

    private void renderStartingIn(TextRenderer renderer, MatrixStack matrices, UIState uiState, int wMid, int hMid) {
        // If starting in is -1 (the halting value)
        if (uiState.startingIn <= -1) {
            // Abort
            return;
        }

        if (uiState.startingIn == 0) {
            DrawableHelper.drawCenteredText(matrices, renderer, "GO!", wMid, hMid, 0xFFFFFFFF);
        } else {
            DrawableHelper.drawCenteredText(matrices, renderer, "Starting in " + uiState.startingIn, wMid, hMid, 0xFFFFFFFF);
        }
    }

    private void renderProgressBar(TextRenderer renderer, MatrixStack matrices, UIState uiState, int wMid, int hMid) {
        // If progress is 0
        if (uiState.progress == -1.0) {
            // Abort
            return;
        }

        int barStart = wMid - (UIState.progressBarWidth / 2);
        int progressWidth = (int) (UIState.progressBarWidth * uiState.progress);

        // Draw background
        DrawableHelper.fill(matrices, barStart, hMid + 10, barStart + UIState.progressBarWidth, hMid + 15, UIState.boxColor);

        // Draw progress
        DrawableHelper.fill(matrices, barStart, hMid + 10, barStart + progressWidth, hMid + 15, 0xFFFFFFFF);

        // Draw text
        DrawableHelper.drawCenteredText(matrices, renderer, "Scanning for spawn points...", wMid, hMid - 15, 0xFFFFFFFF);
    }

    private void renderTimer(TextRenderer renderer,
                             MatrixStack matrices,
                             UIState uiState,
                             ISettingsManager settingsManager,
                             IGameModeManager gameModeManager,
                             int wMid) {

        // If game time should not be rendered
        if (!settingsManager.<Boolean>get(SettingDescription.RENDER_TIMER)) {
            // Abort
            return;
        }

        // Get the game mode
        var gameMode = gameModeManager.getGameMode();

        // Init seconds to show with the time the game is already running
        var secondsToShow = uiState.gameTime;

        // If game mode has no infinite time
        if (!gameMode.hasInfiniteTime()) {
            secondsToShow = (settingsManager.<Long>get(SettingDescription.PLAY_TIME) * 60L) - secondsToShow;
        }

        DrawableHelper.drawCenteredText(matrices, renderer,
                DurationUtils.toString(Duration.ofSeconds(secondsToShow)),
                wMid, 1, 0xFFFFFF);
    }

    private void renderGameOver(TextRenderer renderer,
                                MatrixStack matrices,
                                UIState uiState,
                                ITeamsConfigState teamsConfig,
                                int wMid, int hMid) {

        var winnerTeamId = uiState.lastGameWinnerId;

        if (winnerTeamId == -1) {
            return;
        }

        var winnerTeamOptional = teamsConfig.getTeamOfId(winnerTeamId);

        winnerTeamOptional.ifPresent(winnerTeam -> {

            matrices.push();

            var textColor = 0xFFFFFFFF;

            matrices.scale(2, 2, 2);
            DrawableHelper.drawCenteredText(matrices, renderer, "GAME OVER", wMid / 2, hMid / 2 - 15, textColor);

            matrices.scale(0.5F, 0.5F, 0.5F);
            var beginningString = "Team ";
            var endString = " won!";
            var beginningAndNameString = beginningString + winnerTeam.name();
            var wholeString = beginningAndNameString + endString;
            var widthOfBeginningString = renderer.getWidth(beginningString);
            var widthOfBeginningAndName = renderer.getWidth(beginningAndNameString);
            var widthOfWholeString = renderer.getWidth(wholeString);
            var textStartX = wMid - (widthOfWholeString / 2);
            var textY = hMid + 10;

            renderer.draw(matrices, beginningString, textStartX, textY, textColor);
            renderer.draw(matrices, winnerTeam.name(), textStartX + widthOfBeginningString, textY, winnerTeam.color().getValue());
            renderer.draw(matrices, endString, textStartX + widthOfBeginningAndName, textY, textColor);

            matrices.pop();
        });
    }

    //endregion
}

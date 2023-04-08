package de.kleiner3.lasertag.client.hud;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gui.LasertagHudRenderManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingNames;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.common.util.DurationUtils;
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
        renderTeamList(renderer, matrixStack, renderData);
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
        if (!LasertagGameManager.getInstance().getSettingsManager().<Boolean>get(SettingNames.RENDER_TIMER)) {
            // Abort
            return;
        }

        DrawableHelper.drawCenteredText(matrices, renderer,
                DurationUtils.toString(
                        Duration.ofSeconds((LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingNames.PLAY_TIME) * 60L) - renderData.gameTime)),
                renderData.wMid, LasertagHudRenderManager.textPadding, 0xFFFFFF);
    }

    private void renderTeamList(TextRenderer renderer, MatrixStack matrices, LasertagHudRenderManager renderData) {
        // If team list should not be rendered
        if (!LasertagGameManager.getInstance().getSettingsManager().<Boolean>get(SettingNames.RENDER_TEAM_LIST)) {
            // Abort
            return;
        }

        // Iteration index
        int i = 0;
        for (TeamDto teamDto : LasertagGameManager.getInstance().getTeamManager().teamConfig.values()) {

            // Draw teams on the left
            if (i < 3) {
                // The height to start rendering this box at
                int y = LasertagHudRenderManager.startY + i * (LasertagHudRenderManager.boxHeight + LasertagHudRenderManager.margin);

                renderTeam(renderer, matrices, teamDto, 0, y, renderData);
            } else { // Draw teams on the right
                // The height to start rendering this box at
                int y = LasertagHudRenderManager.startY + (i - 3) * (LasertagHudRenderManager.boxHeight + LasertagHudRenderManager.margin);

                renderTeam(renderer, matrices, teamDto, renderData.width - LasertagHudRenderManager.boxWidth, y, renderData);
            }

            ++i;
        }
    }

    private void renderTeam(TextRenderer renderer, MatrixStack matrices, TeamDto team, int x, int y, LasertagHudRenderManager renderData) {
        var teamScore = 0;

        // Draw the opaque box
        DrawableHelper.fill(matrices, x, y, x + LasertagHudRenderManager.boxWidth, y + LasertagHudRenderManager.boxHeight, LasertagHudRenderManager.boxColor);

        // Draw team name
        renderer.draw(matrices, team.name(), x + LasertagHudRenderManager.textPadding, y + LasertagHudRenderManager.textPadding, team.color().getValue());

        // Draw team members
        int memberY = y + 2 * LasertagHudRenderManager.textPadding + LasertagHudRenderManager.textHeight + 1;
        for (var playerData : renderData.teamMap.get(team.name())) {
            // Draw player name
            renderer.draw(matrices, playerData.x(), x + LasertagHudRenderManager.textPadding, memberY, 0xFFFFFF);

            // Get the players score
            var playerScore = playerData.y();

            // Add the players score to the team score
            teamScore += playerScore;

            // Draw the players score
            String scoreString = Long.toString(playerScore);
            renderer.draw(matrices, scoreString, x + LasertagHudRenderManager.boxWidth - LasertagHudRenderManager.textPadding - renderer.getWidth(scoreString), memberY, 0xFFFFFF);

            // Offset to the next line
            memberY += 2 * LasertagHudRenderManager.textPadding + LasertagHudRenderManager.textHeight - 2;
        }

        // Draw team score
        String scoreString = Integer.toString(teamScore);
        renderer.draw(matrices, scoreString, x + LasertagHudRenderManager.boxWidth - LasertagHudRenderManager.textPadding - renderer.getWidth(scoreString), y + LasertagHudRenderManager.textPadding, 0xFFFFFF);
    }

    //endregion
}

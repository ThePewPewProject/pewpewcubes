package de.kleiner3.lasertag.client.hud;

import de.kleiner3.lasertag.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.settings.SettingNames;
import de.kleiner3.lasertag.types.TeamConfigManager;
import de.kleiner3.lasertag.types.TeamDto;
import de.kleiner3.lasertag.util.DurationUtils;
import de.kleiner3.lasertag.util.Tuple;
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

    public static LasertagHudRenderConfig renderData = new LasertagHudRenderConfig();

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

        // Calculate window size
        renderData.width = client.getWindow().getScaledWidth();
        renderData.wMid = renderData.width / 2;
        renderData.height = client.getWindow().getScaledHeight();
        renderData.hMid = renderData.height / 2;

        // Get the clients text renderer
        TextRenderer renderer = client.textRenderer;

        // Render HUD
        renderTeamList(renderer, matrixStack);
        renderTimer(renderer, matrixStack);
        renderProgressBar(renderer, matrixStack);
        renderStartingIn(renderer, matrixStack);
    }

    private void renderStartingIn(TextRenderer renderer, MatrixStack matrices) {
        // If starting in is -1 (the halting value)
        if (renderData.startingIn == -1) {
            // Abort
            return;
        }

        if (renderData.startingIn == 0) {
            DrawableHelper.drawCenteredText(matrices, renderer, "GO!", renderData.wMid, renderData.hMid, 0xFFFFFFFF);
        } else {
            DrawableHelper.drawCenteredText(matrices, renderer, "Starting in " + renderData.startingIn, renderData.wMid, renderData.hMid, 0xFFFFFFFF);
        }
    }

    private void renderProgressBar(TextRenderer renderer, MatrixStack matrices) {
        // If progress is 0
        if (renderData.progress == 0.0) {
            // Abort
            return;
        }

        int barStart = renderData.wMid - (LasertagHudRenderConfig.progressBarWidth / 2);
        int progressWidth = (int) (LasertagHudRenderConfig.progressBarWidth * renderData.progress);

        // Draw background
        DrawableHelper.fill(matrices, barStart, renderData.hMid + 10, barStart + LasertagHudRenderConfig.progressBarWidth, renderData.hMid + 15, LasertagHudRenderConfig.boxColor);

        // Draw progress
        DrawableHelper.fill(matrices, barStart, renderData.hMid + 10, barStart + progressWidth, renderData.hMid + 15, 0xFFFFFFFF);

        // Draw text
        DrawableHelper.drawCenteredText(matrices, renderer, "Scanning for spawn points...", renderData.wMid, renderData.hMid - 15, 0xFFFFFFFF);
    }

    private void renderTimer(TextRenderer renderer, MatrixStack matrices) {
        // If game time should not be rendered
        if (!(boolean) LasertagSettingsManager.get(SettingNames.RENDER_TIMER)) {
            // Abort
            return;
        }

        DrawableHelper.drawCenteredText(matrices, renderer,
                DurationUtils.toString(
                        Duration.ofSeconds(((long) LasertagSettingsManager.get(SettingNames.PLAY_TIME) * 60L) - renderData.gameTime)),
                renderData.wMid, LasertagHudRenderConfig.textPadding, 0xFFFFFF);
    }

    private void renderTeamList(TextRenderer renderer, MatrixStack matrices) {
        // If team list should not be rendered
        if (!(boolean) LasertagSettingsManager.get(SettingNames.RENDER_TEAM_LIST)) {
            // Abort
            return;
        }

        // Iteration index
        int i = 0;
        for (TeamDto teamDto : TeamConfigManager.teamConfig.values()) {

            // Draw teams on the left
            if (i < LasertagHudRenderConfig.numTeams / 2) {
                // The height to start rendering this box at
                int y = LasertagHudRenderConfig.startY + i * (LasertagHudRenderConfig.boxHeight + LasertagHudRenderConfig.margin);

                renderTeam(renderer, matrices, teamDto, 0, y);
            } else { // Draw teams on the right
                // The height to start rendering this box at
                int y = LasertagHudRenderConfig.startY + (i - (LasertagHudRenderConfig.numTeams / 2)) * (LasertagHudRenderConfig.boxHeight + LasertagHudRenderConfig.margin);

                renderTeam(renderer, matrices, teamDto, renderData.width - LasertagHudRenderConfig.boxWidth, y);
            }

            ++i;
        }
    }

    private void renderTeam(TextRenderer renderer, MatrixStack matrices, TeamDto team, int x, int y) {
        var teamScore = 0;

        // Draw the opaque box
        DrawableHelper.fill(matrices, x, y, x + LasertagHudRenderConfig.boxWidth, y + LasertagHudRenderConfig.boxHeight, LasertagHudRenderConfig.boxColor);

        // Draw team name
        renderer.draw(matrices, team.getName(), x + LasertagHudRenderConfig.textPadding, y + LasertagHudRenderConfig.textPadding, team.getColor().getValue());

        // Draw team members
        int memberY = y + 2 * LasertagHudRenderConfig.textPadding + LasertagHudRenderConfig.textHeight + 1;
        for (Tuple<String, Integer> playerData : renderData.teamMap.get(team.getName())) {
            // Draw player name
            renderer.draw(matrices, playerData.x, x + LasertagHudRenderConfig.textPadding, memberY, 0xFFFFFF);

            // Get the players score
            Integer playerScore = playerData.y;

            // Add the players score to the team score
            teamScore += playerScore;

            // Draw the players score
            String scoreString = Integer.toString(playerScore);
            renderer.draw(matrices, scoreString, x + LasertagHudRenderConfig.boxWidth - LasertagHudRenderConfig.textPadding - renderer.getWidth(scoreString), memberY, 0xFFFFFF);

            // Offset to the next line
            memberY += 2 * LasertagHudRenderConfig.textPadding + LasertagHudRenderConfig.textHeight - 2;
        }

        // Draw team score
        String scoreString = Integer.toString(teamScore);
        renderer.draw(matrices, scoreString, x + LasertagHudRenderConfig.boxWidth - LasertagHudRenderConfig.textPadding - renderer.getWidth(scoreString), y + LasertagHudRenderConfig.textPadding, 0xFFFFFF);
    }
}

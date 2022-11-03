package de.kleiner3.lasertag.client;

import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.types.Colors;
import de.kleiner3.lasertag.util.DurationUtils;
import de.kleiner3.lasertag.util.Tuple;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;

/**
 * Class to implement the custom overlay for the lasertag game
 *
 * @author Étienne Muser
 */
public class LasertagHudOverlay implements HudRenderCallback {
    public static boolean shouldRenderNameTags = true;
    /**
     * The time in seconds that has already elapsed
     */
    public static long gameTime = 0;
    public static Timer gameTimer = null;

    /**
     * Simplified team map to map players and their score to their teams
     */
    public static HashMap<String, LinkedList<Tuple<String, Integer>>> teamMap = new HashMap<>();

    public static double progress = 0.0;

    public static int startingIn = -1;

    private static final int progressBarWidth = 100;
    private static final int numColors = Colors.colorConfig.size();
    private static final int boxColor = 0x88000000;
    private static final int startY = 10;
    private static final int boxHeight = 65;
    private static final int boxWidth = 85;
    private static final int margin = 20;
    private static final int textPadding = 1;
    private static final int textHeight = 9;

    static {
        for (Colors.Color c : Colors.colorConfig.values()) {
            teamMap.put(c.getName(), new LinkedList<>());
        }

//		teamMap.get(Colors.RED).add(new Tuple<String, Integer>("TEST1", 300));
//		teamMap.get(Colors.RED).add(new Tuple<String, Integer>("TEST2", 300));
//		teamMap.get(Colors.RED).add(new Tuple<String, Integer>("TEST3", 300));
//		teamMap.get(Colors.RED).add(new Tuple<String, Integer>("TEST4", 300));
//		teamMap.get(Colors.RED).add(new Tuple<String, Integer>("TEST5", 300));
//		teamMap.get(Colors.RED).add(new Tuple<String, Integer>("TEST6", 300));
    }

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

        // Get the clients text renderer
        TextRenderer renderer = client.textRenderer;

        // Get the logical width of the window
        int width = client.getWindow().getScaledWidth();
        int wMid = width / 2;
        int height = client.getWindow().getScaledHeight();
        int hMid = height / 2;

        if (LasertagConfig.getInstance().isRenderTeamList()) {
            // Iteration index
            int i = 0;
            for (Colors.Color teamColor : Colors.colorConfig.values()) {
                // The team score
                int teamScore = 0;

                // Draw teams on the left
                if (i < numColors / 2) {
                    // The height to start rendering this box at
                    int y = startY + i * (boxHeight + margin);

                    // Draw the opaque box
                    DrawableHelper.fill(matrixStack, 0, y, boxWidth, y + boxHeight, boxColor);

                    // Draw team name
                    renderer.draw(matrixStack, teamColor.getName(), textPadding, y + textPadding, teamColor.getValue());

                    // Draw team members
                    int memberY = y + 2 * textPadding + textHeight + 1;
                    for (Tuple<String, Integer> playerData : teamMap.get(teamColor.getName())) {
                        // Draw player name
                        renderer.draw(matrixStack, playerData.x, textPadding, memberY, 0xFFFFFF);

                        // Get the players score
                        Integer playerScore = playerData.y;

                        // Add the players score to the team score
                        teamScore += playerScore;

                        // Draw the players score
                        String scoreString = Integer.toString(playerScore);
                        renderer.draw(matrixStack, scoreString, boxWidth - textPadding - renderer.getWidth(scoreString), memberY, 0xFFFFFF);

                        // Offset to the next line
                        memberY += 2 * textPadding + textHeight - 2;
                    }

                    // Draw team score
                    String scoreString = Integer.toString(teamScore);
                    renderer.draw(matrixStack, scoreString, boxWidth - textPadding - renderer.getWidth(scoreString), y + textPadding, 0xFFFFFF);
                } else { // Draw teams on the right
                    // The height to start rendering this box at
                    int y = startY + (i - (numColors / 2)) * (boxHeight + margin);

                    // Draw the opaque box
                    DrawableHelper.fill(matrixStack, width - boxWidth, y, width, y + boxHeight, boxColor);

                    // Draw team name
                    renderer.draw(matrixStack, teamColor.getName(), width - textPadding - renderer.getWidth(teamColor.getName()), y + textPadding, teamColor.getValue());

                    // Draw team members
                    int memberY = y + 2 * textPadding + textHeight + 1;
                    for (Tuple<String, Integer> playerData : teamMap.get(teamColor.getName())) {
                        // Draw player name
                        renderer.draw(matrixStack, playerData.x, width - textPadding - renderer.getWidth(playerData.x), memberY, 0xFFFFFF);

                        // Get the players score
                        Integer playerScore = playerData.y;

                        // Add the players score to the team score
                        teamScore += playerScore;

                        // Draw the players score
                        String scoreString = Integer.toString(playerScore);
                        renderer.draw(matrixStack, scoreString, width - boxWidth + textPadding, memberY, 0xFFFFFF);

                        // Offset to the next line
                        memberY += 2 * textPadding + textHeight - 2;
                    }

                    // Draw team score
                    String scoreString = Integer.toString(teamScore);
                    renderer.draw(matrixStack, scoreString, width - boxWidth + textPadding, y + textPadding, 0xFFFFFF);
                }

                ++i;
            }
        }

        // If game time should be rendered
        if (LasertagConfig.getInstance().isRenderTimer()) {
            DrawableHelper.drawCenteredText(matrixStack, renderer, DurationUtils.toString(Duration.ofSeconds((LasertagConfig.getInstance().getPlayTime() * 60) - gameTime)), wMid, textPadding, 0xFFFFFF);
        }

        // Draw progress bar
        if (progress != 0.0) {
            int barStart = wMid - (progressBarWidth / 2);
            int progressWidth = (int)(progressBarWidth * progress);

            // Draw background
            DrawableHelper.fill(matrixStack, barStart, hMid + 10, barStart + progressBarWidth, hMid + 15, boxColor);

            // Draw progress
            DrawableHelper.fill(matrixStack, barStart, hMid + 10, barStart + progressWidth, hMid + 15, 0xFFFFFFFF);

            // Draw text
            DrawableHelper.drawCenteredText(matrixStack, renderer, "Scanning for spawn points...", wMid, hMid - 15, 0xFFFFFFFF);
        }

        // Draw starting game in
        if (startingIn != -1) {
            if (startingIn == 0) {
                DrawableHelper.drawCenteredText(matrixStack, renderer, "GO!", wMid, hMid, 0xFFFFFFFF);
            } else {
                DrawableHelper.drawCenteredText(matrixStack, renderer, "Starting in " + startingIn, wMid, hMid, 0xFFFFFFFF);
            }
        }
    }
}

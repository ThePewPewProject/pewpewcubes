package de.kleiner3.lasertag.client.hud;

import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.common.util.AdvancedDrawableHelper;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Class to implement the team list hud overlay
 *
 * @author Étienne Muser
 */
public class TeamListHudOverlay extends AdvancedDrawableHelper {

    private static final int MAX_NUMBER_TEAMS_PER_ROW = 4;
    private static final int TEAM_WIDTH = 110;
    private static final int TEAM_PADDING = 5;
    private static final int TEXT_PADDING = 2;
    private static final int INTRA_PLAYER_PADDING = 0;
    private static final int TEAM_LIST_TOP_PADDING = 15;
    private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;

    /**
     * Renders the current team list into the matrix stack
     * @param matrices
     * @param scaledWindowWidth
     */
    public void render(MatrixStack matrices, int scaledWindowWidth) {

        drawPlayersWithoutTeam(matrices, scaledWindowWidth);

        // Get the team config
        var teamConfig = LasertagGameManager.getInstance().getTeamManager().teamConfig;

        // Get the HUD render data
        var renderData = LasertagGameManager.getInstance().getHudRenderManager();

        // Get a list of Tuple<TeamDto, List<Tuple<String, Long>>> (One entry is a team) which are not empty
        var teams = renderData.teamMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 0)
                .map(entry -> new Tuple<TeamDto, List<Tuple<String, Long>>>(teamConfig.get(entry.getKey()), entry.getValue()))
                .toList();
        var numberOfTeams = teams.size();

        // If there is no team to show
        if (numberOfTeams == 0) {
            var text = "No teams to show";
            var width = TEXT_RENDERER.getWidth(text);
            TEXT_RENDERER.drawWithShadow(matrices, text, scaledWindowWidth / 2.0F - width / 2.0F, 20.0F, 0xFFFFFF);
            return;
        }

        var maxNumberOfPlayersInTeam = teams.stream()
                .map(team -> team.y().size())
                .max(Comparator.comparingInt(i -> i))
                .get();

        var numberOfRows = (int)Math.ceil((float)numberOfTeams / (float)MAX_NUMBER_TEAMS_PER_ROW);
        var numberOfcolumns = numberOfTeams < MAX_NUMBER_TEAMS_PER_ROW ? numberOfTeams : MAX_NUMBER_TEAMS_PER_ROW;
        var teamListWidth = (numberOfcolumns * TEAM_WIDTH) + ((numberOfcolumns + 1) * TEAM_PADDING);
        var teamHeight = TEXT_PADDING + TEXT_RENDERER.fontHeight + TEXT_PADDING + (maxNumberOfPlayersInTeam * (INTRA_PLAYER_PADDING + TEXT_RENDERER.fontHeight));
        var teamListHeight = (numberOfRows * teamHeight) + ((numberOfRows + 1) * TEAM_PADDING);
        var startX = (int)((scaledWindowWidth / 2.0) - (teamListWidth / 2.0));

        // Draw the background rectangle
        fill(matrices, startX, TEAM_LIST_TOP_PADDING, startX + teamListWidth, TEAM_LIST_TOP_PADDING + teamListHeight, 0x66000000);

        var teamIterator = teams.iterator();
        for (int row = 0; row < numberOfRows; ++row) {
            for (int column = 0; column < MAX_NUMBER_TEAMS_PER_ROW; ++column) {
                if (teamIterator.hasNext() == false) {
                    break;
                }

                var team = teamIterator.next();

                var x = column * (TEAM_PADDING + TEAM_WIDTH);
                var y = row * (TEAM_PADDING + teamHeight);

                drawTeam(matrices, team, startX + x, TEAM_LIST_TOP_PADDING + y, teamHeight);
            }
        }
    }

    /**
     * Draws team at given start coordinates
     * @param matrices
     * @param team
     * @param startX
     * @param startY
     * @param teamHeight
     */
    private void drawTeam(MatrixStack matrices,
                                 Tuple<TeamDto, List<Tuple<String, Long>>> team,
                                 int startX,
                                 int startY,
                                 int teamHeight) {
        var rectangleStartX = startX + TEAM_PADDING;
        var rectangleStartY = startY + TEAM_PADDING;
        var teamDto = team.x();
        var teamScore = team.y().stream().mapToLong(t -> t.y()).sum();
        var textHeight = TEXT_RENDERER.fontHeight;

        // Draw rectangle of team
        drawRectangle(matrices, rectangleStartX, rectangleStartY, rectangleStartX + TEAM_WIDTH, rectangleStartY + teamHeight, 0xAAFFFFFF);

        // Draw team name
        drawWithShadow(matrices, TEXT_RENDERER, Text.literal(teamDto.name()).asOrderedText(), rectangleStartX + TEXT_PADDING + 1, rectangleStartY + TEXT_PADDING + 1, teamDto.color().getValue());

        // Draw team score
        var teamScoreString = Long.toString(teamScore);
        var scoreStartX = rectangleStartX + TEAM_WIDTH - TEXT_RENDERER.getWidth(teamScoreString) - TEXT_PADDING;
        drawWithShadow(matrices, TEXT_RENDERER, Text.literal(teamScoreString).asOrderedText(), scoreStartX, rectangleStartY + TEXT_PADDING + 1, 0xFFFFFFFF);

        var playerY = rectangleStartY + TEXT_PADDING + textHeight + TEXT_PADDING;
        for (var player : team.y()) {

            // Draw player name
            TEXT_RENDERER.draw(matrices, player.x(), rectangleStartX + TEXT_PADDING + 1, playerY, 0xFFFFFFFF);

            // Draw player score
            var playerScoreString = Long.toString(player.y());
            var playerScoreStartX = rectangleStartX + TEAM_WIDTH - TEXT_RENDERER.getWidth(playerScoreString) - TEXT_PADDING;
            TEXT_RENDERER.draw(matrices, playerScoreString, playerScoreStartX, playerY, 0xFFFFFFFF);

            playerY += (INTRA_PLAYER_PADDING + textHeight);
        }
    }

    private void drawPlayersWithoutTeam(MatrixStack matrices, int scaledWindowWidth) {

        // Get the players without team
        var playersWithoutTeam = MinecraftClient.getInstance().player.networkHandler.getPlayerList().stream()
                .filter(playerListEntry -> !playerHasTeam(playerListEntry.getProfile().getName()))
                .toList();

        var startX = scaledWindowWidth - TEAM_WIDTH;
        var height = TEXT_PADDING + TEXT_RENDERER.fontHeight + TEXT_PADDING + (playersWithoutTeam.size() * (INTRA_PLAYER_PADDING + TEXT_RENDERER.fontHeight));
    }

    private boolean playerHasTeam(String playerUsername) {
        var teamList = LasertagGameManager.getInstance().getHudRenderManager().teamMap;

        for (var team : teamList.values()) {
            for (var player : team) {
                if (player.x().equals(playerUsername)) {
                    return true;
                }
            }
        }
        return false;
    }
}

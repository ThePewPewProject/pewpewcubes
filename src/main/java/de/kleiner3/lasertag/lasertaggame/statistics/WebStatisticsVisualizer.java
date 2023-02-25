package de.kleiner3.lasertag.lasertaggame.statistics;

import com.google.gson.Gson;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.resource.WebResourceManager;
import de.kleiner3.lasertag.common.util.FileIO;
import de.kleiner3.lasertag.lasertaggame.statistics.mojangsessionaccess.PlayerInfoDto;
import de.kleiner3.lasertag.lasertaggame.statistics.mojangsessionaccess.ProfileTextureDto;
import de.kleiner3.lasertag.lasertaggame.statistics.mojangsessionaccess.SessionPlayerProfileDto;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

/**
 * Builds a web page to visualize game statistics
 *
 * @author Étienne Muser
 */
public class WebStatisticsVisualizer {
    private static final Path TARGET_PATH;

    // Initialize target path
    static {
        Path target;

        // I have to use try/catch because for some reason FabricLoader dosn't support checking if it is initialized correctly
        try {
            target = Path.of(FabricLoader.getInstance().getGameDir().toString(), "lasertag_last_games_stats");
        } catch (IllegalStateException ignored) {
            target = Path.of(System.getProperty("user.dir"), "build");
        }

        TARGET_PATH = target;
    }

    private static final Identifier REPLACE_ID = new Identifier(LasertagMod.ID, "statistics_go_here");

    public static String build(GameStats stats, WebResourceManager resourceManager) {
        // The path to the generated index.html file
        String resultPath = null;

        // Get web page template from resource manager
        var template = resourceManager.getWebSite(new Identifier("web/statistics_template"));

        // Check
        if (template == null) {
            return null;
        }

        // Build web page
        for (var fileTuple : template) {

            // if it is the html file
            if (fileTuple.x().getPath().endsWith(".html")) {
                // Read the file
                String fileContents;
                try {
                    fileContents = FileIO.readAllFile(fileTuple.y().getInputStream());
                } catch (IOException ex) {
                    LasertagMod.LOGGER.error("Exception in WebStatisticsVisualizer.build:", ex);
                    return null;
                }

                fileContents = buildHtml(fileContents, stats);

                // Write file
                resultPath = Path.of(TARGET_PATH.toString(), fileTuple.x().getPath()).toString();
                try {
                    var ignored = FileIO.createNewFile(resultPath);

                    Files.writeString(Path.of(resultPath), fileContents);
                } catch (IOException ex) {
                    LasertagMod.LOGGER.error("Exception in WebStatisticsVisualizer.build:", ex);
                    return null;
                }

                continue;
            }

            try {
                var copyToPath = Path.of(TARGET_PATH.toString(), fileTuple.x().getPath());

                var ignored = FileIO.createNewFile(copyToPath.toString());

                Files.copy(fileTuple.y().getInputStream(), copyToPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                LasertagMod.LOGGER.error("Failed to copy file '" + fileTuple.y().toString() + "' of statistics visualization.", ex);
                return null;
            }
        }

        return resultPath;
    }

    private static String buildHtml(String html, GameStats stats) {
        // build html to insert
        var builder = new StringBuilder();

        buildTeamScores(builder, stats);

        buildPlayerScores(builder, stats);

        buildTeamByPlayersScores(builder, stats);

        // Find and replace
        return html.replaceAll("#" + REPLACE_ID + "#", builder.toString());
    }

    private static void buildTeamScores(StringBuilder builder, GameStats stats) {
        buildTableHeader(builder, "Team scores", "Team");

        int teamNo = 1;
        for (var team : stats.teamScores) {
            buildTableRow(builder, teamNo++, team.x(), team.y());
        }

        buildTableEnd(builder);
    }

    private static void buildPlayerScores(StringBuilder builder, GameStats stats) {
        buildTableHeader(builder, "Player scores", "Player");

        int playerNo = 1;
        for (var player : stats.playerScores) {
            var playerHtml = getSkinNameHtml(player.x());

            buildTableRow(builder, playerNo++, playerHtml, player.y());
        }

        buildTableEnd(builder);
    }

    private static void buildTeamByPlayersScores(StringBuilder builder, GameStats stats) {
        for (var team : stats.teamPlayerScores.entrySet()) {
            buildTableHeader(builder, team.getKey(), "Player");

            int playerNo = 1;
            for (var player : team.getValue()) {
                var playerHtml = getSkinNameHtml(player.x());

                buildTableRow(builder, playerNo++, playerHtml, player.y());
            }

            buildTableEnd(builder);
        }
    }

    private static void buildTableRow(StringBuilder builder, int place, String nameHtml, int score) {
        builder.append("<tr><th scope=\"row\">");
        builder.append(place);
        builder.append("</th><td>");
        builder.append(nameHtml);
        builder.append("</td><td>");
        builder.append(score);
        builder.append("</td></tr>");
    }

    private static void buildTableHeader(StringBuilder builder, String tableTitle, String nameColHeader) {
        builder.append("<div class=\"container py-5\"><h5 class=\"h5\">");
        builder.append(tableTitle);
        builder.append("</h5><table class=\"table text-light\"><col width=\"15%\"/><thead><tr><th scope=\"col\">#</th><th scope=\"col\">");
        builder.append(nameColHeader);
        builder.append("</th><th scope=\"col\">Score</th></tr></thead><tbody>");
    }

    private static void buildTableEnd(StringBuilder builder) {
        builder.append("</tbody></table></div>");
    }

    private static String getSkinNameHtml(String playerName) {
        try {
            var skinUrl = getSkinUrlFromPlayerName(playerName);

            return  "<div class=\"face-container\"><img src='" + skinUrl + "'></div>\n\n" + playerName;

        } catch (Exception ignored) {
            return "<div class=\"face-container\"></div>" + playerName;
        }
    }

    private static String getSkinUrlFromPlayerName(String playerName) throws Exception {
        // Url to player info
        var playerInfoUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);

        // Create input stream
        var playerInfoInputStreamReader = new InputStreamReader(playerInfoUrl.openStream());

        // Get uuid of player
        var uuid = new Gson().fromJson(playerInfoInputStreamReader, PlayerInfoDto.class).id;

        // Url to session profile of player
        var sessionProfileUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);

        // Create input stream
        var sessionProfileInputStreamReader = new InputStreamReader(sessionProfileUrl.openStream());

        // Get base64 encoded texture json
        var encodedTextureJson = new Gson().fromJson(sessionProfileInputStreamReader, SessionPlayerProfileDto.class).properties[0].value;

        // Decode
        var decodedTextureJson = new String(Base64.getDecoder().decode(encodedTextureJson), StandardCharsets.UTF_8);

        // Get skin url
        return new Gson().fromJson(decodedTextureJson, ProfileTextureDto.class).textures.get("SKIN").url;
    }
}

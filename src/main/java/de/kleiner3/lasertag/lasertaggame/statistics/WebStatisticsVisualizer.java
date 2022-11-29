package de.kleiner3.lasertag.lasertaggame.statistics;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.resource.ResourceManagers;
import de.kleiner3.lasertag.util.FileIO;
import de.kleiner3.lasertag.util.serialize.PlayerInfoDto;
import de.kleiner3.lasertag.util.serialize.ProfileTextureDto;
import de.kleiner3.lasertag.util.serialize.SessionPlayerProfileDto;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
    private static final Path TARGET_PATH = Path.of(FabricLoader.getInstance().getGameDir().toString(), "lasertag_last_games_stats");
    private static final Identifier REPLACE_ID = new Identifier(LasertagMod.ID, "statistics_go_here");

    public static String build(GameStats stats) {
        // The path to the generated index.html file
        String resultPath = null;

        // Get web page template from resource manager
        var template = ResourceManagers.WEB_RESOURCE_MANAGER.getWebSite(new Identifier("web/statistics_template"));

        // Build web page
        for (var fileTuple : template) {
            var copyToPath = Path.of(TARGET_PATH.toString(), fileTuple.x.getPath());

            // if it is the html file
            if (fileTuple.x.getPath().endsWith(".html")) {
                // Read the file
                String fileContents = null;
                try {
                    fileContents = FileIO.readAllFile(fileTuple.y.getInputStream());
                } catch (IOException ex) {
                    LasertagMod.LOGGER.error("Exception in WebStatisticsVisualizer.build:", ex);
                    return null;
                }

                fileContents = buildHtml(fileContents, stats);

                // Write file
                resultPath = Path.of(TARGET_PATH.toString(), fileTuple.x.getPath()).toString();;
                try {
                    // Create file (File should not exist as the whole directory got deleted earlier)
                    var file = FileIO.createNewFile(resultPath);

                    FileIO.writeAllFile(file, fileContents);
                } catch (IOException ex) {
                    LasertagMod.LOGGER.error("Exception in WebStatisticsVisualizer.build:", ex);
                    return null;
                }

                continue;
            }

            try {
                var ignored = FileIO.createNewFile(copyToPath.toString());

                Files.copy(fileTuple.y.getInputStream(), copyToPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                LasertagMod.LOGGER.error("Failed to copy file '" + fileTuple.y.toString() + "' of statistics visualization.", ex);
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
        return html.replaceAll("#" + REPLACE_ID.toString() + "#", builder.toString());
    }

    private static void buildTeamScores(StringBuilder builder, GameStats stats) {
        buildTableHeader(builder, "Team scores", "Team");

        int teamNo = 1;
        for (var team : stats.teamScores) {
            buildTableRow(builder, teamNo++, team.x, team.y);
        }

        buildTableEnd(builder);
    }

    private static void buildPlayerScores(StringBuilder builder, GameStats stats) {
        buildTableHeader(builder, "Player scores", "Player");

        int playerNo = 1;
        for (var player : stats.playerScores) {
            var playerHtml = getSkinNameHtml(player.x);

            buildTableRow(builder, playerNo++, playerHtml, player.y);
        }

        buildTableEnd(builder);
    }

    private static void buildTeamByPlayersScores(StringBuilder builder, GameStats stats) {
        for (var team : stats.teamPlayerScores.entrySet()) {
            buildTableHeader(builder, team.getKey(), "Player");

            int playerNo = 1;
            for (var player : team.getValue()) {
                var playerHtml = getSkinNameHtml(player.x);

                buildTableRow(builder, playerNo++, playerHtml, player.y);
            }

            buildTableEnd(builder);
        }
    }

    private static void buildTableRow(StringBuilder builder, int place, String nameHtml, int score) {
        builder.append(         "<tr>" +
                                    "<th scope=\"row\">" +
                                        place +
                                    "</th>" +
                                    "<td>" +
                                        nameHtml +
                                    "</td>" +
                                    "<td>" +
                                        score +
                                    "</td>" +
                                "</tr>");
    }

    private static void buildTableHeader(StringBuilder builder, String tableTitle, String nameColHeader) {
        builder.append("<div class=\"container py-5\">" +
                            "<h5 class=\"h5\">" + tableTitle + "</h5>" +
                            "<table class=\"table text-light\">" +
                                "<col width=\"15%\"/>" +
                                "<thead>" +
                                    "<tr>" +
                                        "<th scope=\"col\">#</th>" +
                                        "<th scope=\"col\">" + nameColHeader + "</th>" +
                                        "<th scope=\"col\">Score</th>" +
                                    "</tr>" +
                                "</thead>" +
                                "<tbody>");
    }

    private static void buildTableEnd(StringBuilder builder) {
        builder.append(         "</tbody>" +
                            "</table>" +
                        "</div>");
    }

    private static String getSkinNameHtml(String playerName) {
        try {
            var skinUrl = getSkinUrlFromPlayerName(playerName);

            return  "<div class=\"face-container\">" +
                        "<img src='" + skinUrl + "'>" +
                    "</div>\n\n" +
                    playerName;

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

package de.kleiner3.lasertag.networking.client.callbacks;

import com.google.gson.Gson;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.statistics.GameStats;
import de.kleiner3.lasertag.lasertaggame.statistics.WebStatisticsVisualizer;
import de.kleiner3.lasertag.resource.ResourceManagers;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.mightypork.rpack.utils.DesktopApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Callback to handle the game statistics incoming network event
 *
 * @author Étienne Muser
 */
public class GameStatisticsIncomingCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the managers
            var gameManager = LasertagGameManager.getInstance();
            var hudRenderManager = gameManager.getHudRenderManager();
            var settingManager = gameManager.getSettingsManager();

            // Read from buffer
            var json = buf.readString();

            // Unpack json
            var stats = new Gson().fromJson(json, GameStats.class);

            if (!stats.teamScores.isEmpty()) {

                // Get the winner team id
                var winnerTeamId = gameManager.getGameModeManager().getGameMode().getWinnerTeamId();

                // If something went wrong
                if (winnerTeamId == -1) {
                    LasertagMod.LOGGER.warn("Something went wrong while deciding what team won.");
                }

                // Set the winner team id
                hudRenderManager.lastGameWinnerId = winnerTeamId;

                var gameOverOverlayTimer = ThreadUtil.createScheduledExecutor("lasertag-client-game-over-timer-%d");
                gameOverOverlayTimer.schedule(() -> {
                    hudRenderManager.lastGameWinnerId = -1;
                    gameOverOverlayTimer.shutdownNow();
                }, 5, TimeUnit.SECONDS);
            }

            // If should generate file
            if (settingManager.<Boolean>get(SettingDescription.GEN_STATS_FILE)) {

                // Generate file
                var generatedFilePath = WebStatisticsVisualizer.build(stats, ResourceManagers.WEB_RESOURCE_MANAGER);

                // If generation failed
                if (generatedFilePath == null) {
                    var msg = "Failed to generate statistics file.";

                    LasertagMod.LOGGER.error(msg);
                    client.player.sendMessage(Text.translatable(msg)
                            .fillStyle(Style.EMPTY.withColor(Formatting.RED)), false);

                    return;
                }

                // If should automatically open file
                if (settingManager.<Boolean>get(SettingDescription.AUTO_OPEN_STATS_FILE)) {

                    try {
                        DesktopApi.open(new File(generatedFilePath));
                    } catch (Exception e) {

                        // Log error
                        LasertagMod.LOGGER.error("Failed to open statistics from '" + generatedFilePath + "': " + e.getMessage());

                        // Notify player
                        client.player.sendMessage(Text.translatable("Failed to open statistics from " + generatedFilePath)
                                .fillStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                    }

                } else {

                    // Notify player about generation of file
                    client.player.sendMessage(Text.translatable("Game statistics saved to " + generatedFilePath)
                            .fillStyle(Style.EMPTY.withColor(Formatting.WHITE)), false);
                }
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in GameStatisticsIncomingCallback", ex);
            throw ex;
        }
    }
}

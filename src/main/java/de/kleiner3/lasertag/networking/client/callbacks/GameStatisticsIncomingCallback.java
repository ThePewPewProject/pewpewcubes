package de.kleiner3.lasertag.networking.client.callbacks;

import com.google.gson.Gson;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
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
            var gameManager = client.world.getClientLasertagManager();
            var uiState = gameManager.getSyncedState().getUIState();
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
                uiState.lastGameWinnerId = winnerTeamId;

                var gameOverOverlayTimer = ThreadUtil.createScheduledExecutor("client-lasertag-game-over-timer-%d");
                gameOverOverlayTimer.schedule(() -> {
                    uiState.lastGameWinnerId = -1;
                    gameOverOverlayTimer.shutdownNow();
                }, 5, TimeUnit.SECONDS);
            }

            // If should generate file
            if (settingManager.<Boolean>get(SettingDescription.GEN_STATS_FILE)) {

                // Generate file
                var generatedFilePath = WebStatisticsVisualizer.build(stats, ResourceManagers.WEB_RESOURCE_MANAGER);

                // If generation failed
                if (generatedFilePath == null) {

                    LasertagMod.LOGGER.error("Failed to generate statistics file.");
                    client.player.sendMessage(Text.translatable("chat.messae.game_stats_generate_failed")
                            .fillStyle(Style.EMPTY.withColor(Formatting.RED)), false);

                    return;
                }

                // Set the stats file
                client.setStatsFilePath(generatedFilePath.toString());

                // If should automatically open file
                if (settingManager.<Boolean>get(SettingDescription.AUTO_OPEN_STATS_FILE)) {

                    try {
                        DesktopApi.open(generatedFilePath.toFile());
                    } catch (Exception e) {

                        // Log error
                        LasertagMod.LOGGER.error("Failed to open statistics from '" + generatedFilePath + "': " + e.getMessage());

                        // Notify player
                        client.player.sendMessage(
                                Text.translatable("chat.message.game_stats_open_failed",
                                                generatedFilePath.toString())
                                .fillStyle(Style.EMPTY.withColor(Formatting.RED)), false);
                    }

                } else {

                    // Notify player about generation of file
                    client.player.sendMessage(Text.translatable("chat.message.game_stats_saved"), false);
                }
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in GameStatisticsIncomingCallback", ex);
            throw ex;
        }
    }
}

package de.kleiner3.lasertag.networking.client.callbacks;

import com.google.gson.Gson;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.statistics.GameStats;
import de.kleiner3.lasertag.lasertaggame.statistics.WebStatisticsVisualizer;
import de.kleiner3.lasertag.resource.ResourceManagers;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingNames;
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

/**
 * Callback to handle the game statistics incoming network event
 *
 * @author Étienne Muser
 */
public class GameStatisticsIncomingCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // Read from buffer
        var json = buf.readString();

        // Unpack json
        var stats = new Gson().fromJson(json, GameStats.class);

        // If should generate file
        if (LasertagGameManager.getInstance().getSettingsManager().<Boolean>get(SettingNames.GEN_STATS_FILE)) {

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
            if (LasertagGameManager.getInstance().getSettingsManager().<Boolean>get(SettingNames.AUTO_OPEN_STATS_FILE)) {

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
    }
}

package de.kleiner3.lasertag.networking.client.callbacks;

import com.google.gson.Gson;
import de.kleiner3.lasertag.client.hud.LasertagHudOverlay;
import de.kleiner3.lasertag.client.hud.LasertagHudRenderConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the lasertag hud sync network event
 *
 * @author Étienne Muser
 */
public class LasertagHudSyncCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // Get json string
        var jsonString = buf.readString();

        // Deserialize json
        var renderConfig = new Gson().fromJson(jsonString, LasertagHudRenderConfig.class);

        // Dispose old render data
        LasertagHudOverlay.renderData.dispose();

        // Set the render config
        LasertagHudOverlay.renderData = renderConfig;

        // Get if game is running
        var gameRunning = buf.readBoolean();

        // If game is running
        if (gameRunning) {
            // If pre game countdown is already over
            if (renderConfig.startingIn <= -1) {
                // Start game timer
                LasertagHudOverlay.renderData.startGameTimer(renderConfig.gameTime);
            } else {
                // Start pre game timer
                LasertagHudOverlay.renderData.startPreGameCountdownTimer(renderConfig.startingIn);
            }
        }
    }
}

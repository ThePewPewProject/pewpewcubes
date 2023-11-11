package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the game manager sync network event
 *
 * @author Étienne Muser
 */
public class LasertagGameManagerSyncCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get json string
            var jsonString = buf.readString();

            // Get if game is running
            var gameRunning = buf.readBoolean();

            // Deserialize
            var gameManager = LasertagGameManager.fromJson(jsonString);

            // reset old game manager
            LasertagGameManager.reset();

            // Set
            LasertagGameManager.set(gameManager);

            // Get the render config
            var renderData = LasertagGameManager.getInstance().getHudRenderManager();

            // If game is running
            if (gameRunning) {
                // If pre game countdown is already over
                if (renderData.startingIn <= -1) {
                    // Start game timer
                    renderData.startGameTimer(renderData.gameTime);
                } else {
                    // Start pre game timer
                    renderData.startPreGameCountdownTimer(renderData.startingIn);
                }
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in LasertagGameManagerSyncCallback", ex);
            throw ex;
        }
    }
}

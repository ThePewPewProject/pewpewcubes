package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the lasertag game started network event
 *
 * @author Étienne Muser
 */
public class LasertagGameStartedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            var renderData = LasertagGameManager.getInstance().getHudRenderManager();

            renderData.progress = 0.0;
            renderData.shouldRenderNameTags = false;
            renderData.isGameRunning = true;

            // Start pregame count down timer
            renderData.startPreGameCountdownTimer(LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PREGAME_DURATION));
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in LasertagGameStartedCallback", ex);
            throw ex;
        }
    }
}

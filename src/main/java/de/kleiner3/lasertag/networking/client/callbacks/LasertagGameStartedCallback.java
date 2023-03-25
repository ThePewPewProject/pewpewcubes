package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.client.hud.LasertagHudOverlay;
import de.kleiner3.lasertag.lasertaggame.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.lasertaggame.settings.SettingNames;
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
        // TODO: Assert that this method does nothing if game is already running
        LasertagHudOverlay.renderData.progress = 0.0;
        LasertagHudOverlay.renderData.shouldRenderNameTags = false;

        // Start pregame count down timer
        LasertagHudOverlay.renderData.startPreGameCountdownTimer((long) LasertagSettingsManager.get(SettingNames.START_TIME));
    }
}

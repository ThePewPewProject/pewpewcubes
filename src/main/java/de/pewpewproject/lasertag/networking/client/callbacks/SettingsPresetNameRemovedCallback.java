package de.pewpewproject.lasertag.networking.client.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.client.screen.LasertagGameManagerSettingsPresetsScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the lasertag settings preset name removed network event
 *
 * @author Étienne Muser
 */
public class SettingsPresetNameRemovedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = client.world.getClientLasertagManager();
            var presetNamesManager = gameManager.getSettingsPresetsNameManager();

            presetNamesManager.removePresetName(buf.readString());

            if (client.currentScreen instanceof LasertagGameManagerSettingsPresetsScreen lasertagGameManagerSettingsPresetsScreen) {
                lasertagGameManagerSettingsPresetsScreen.resetList();
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in SettingsPresetNameRemovedCallback", ex);
            throw ex;
        }
    }
}

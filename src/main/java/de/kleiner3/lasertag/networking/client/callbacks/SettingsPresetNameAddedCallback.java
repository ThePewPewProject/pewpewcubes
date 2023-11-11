package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.client.screen.LasertagGameManagerSettingsPresetsScreen;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the lasertag preset name added network event
 *
 * @author Étienne Muser
 */
public class SettingsPresetNameAddedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            LasertagGameManager.getInstance().getPresetsNameManager().addPresetName(null, buf.readString());

            if (client.currentScreen instanceof LasertagGameManagerSettingsPresetsScreen lasertagGameManagerSettingsPresetsScreen) {
                lasertagGameManagerSettingsPresetsScreen.resetList();
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in SettingsPresetNameAddedCallback", ex);
            throw ex;
        }
    }
}

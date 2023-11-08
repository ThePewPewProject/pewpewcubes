package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.client.screen.LasertagGameManagerSettingsScreen;
import de.kleiner3.lasertag.common.util.ConverterUtil;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the lasertag setting changed network event
 *
 * @author Étienne Muser
 */
public class LasertagSettingChangedCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // Read from buffer
        var settingsName = buf.readString();
        var value = buf.readString();

        // Get the setting description
        var settingDescription = SettingDescription.byName(settingsName);

        if (settingDescription.isEmpty()) {

            LasertagMod.LOGGER.error("Unrecognized setting name '{}'", settingsName);
            return;
        }

        Object primitive;
        if (settingDescription.get().getDataType().isEnum()) {

            // If is enum, simply use the string
            primitive = value;
        } else {
            // Convert to primitive type
            primitive = ConverterUtil.stringToPrimitiveType(value);
        }

        LasertagGameManager.getInstance().getSettingsManager().set(null, settingsName, primitive);

        if (client.currentScreen instanceof LasertagGameManagerSettingsScreen lasertagGameManagerSettingsScreen) {
            lasertagGameManagerSettingsScreen.resetList();
        }
    }
}

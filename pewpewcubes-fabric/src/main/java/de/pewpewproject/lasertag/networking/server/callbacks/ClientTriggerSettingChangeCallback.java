package de.pewpewproject.lasertag.networking.server.callbacks;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import static de.pewpewproject.lasertag.lasertaggame.settings.SettingDataType.BOOL;
import static de.pewpewproject.lasertag.lasertaggame.settings.SettingDataType.LONG;

/**
 * Callback for the client trigger setting change network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerSettingChangeCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        try {

            // Get the game managers
            var gameManager = server.getOverworld().getServerLasertagManager();
            var settingsManager = gameManager.getSettingsManager();

            // If a game is running
            if (gameManager.isGameRunning()) {
                // Cannot change settings in-game
                return;
            }

            // Get the setting name
            var settingEnumName = buf.readString();

            // Get the setting description
            var settingDescription = SettingDescription.valueOf(settingEnumName);

            Object settingValue = null;
            if (settingDescription.getDataType().equals(BOOL)) {
                settingValue = buf.readBoolean();
            } else if (settingDescription.getDataType().equals(LONG)) {
                settingValue = buf.readLong();
            } else if (settingDescription.getDataType().isEnum()) {
                settingValue = buf.readString();
            } else {
                LasertagMod.LOGGER.error("ClientTriggerSettingChangeCallback: Unrecognised setting datatype '{}'", settingDescription.getDataType());
            }

            if (settingValue != null) {
                settingsManager.set(settingDescription.getName(), settingValue);
            }
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("Error in ClientTriggerSettingChangeCallback", ex);
            throw ex;
        }
    }
}

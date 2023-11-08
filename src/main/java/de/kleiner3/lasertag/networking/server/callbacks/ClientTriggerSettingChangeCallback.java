package de.kleiner3.lasertag.networking.server.callbacks;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import static de.kleiner3.lasertag.lasertaggame.management.settings.SettingDataType.BOOL;
import static de.kleiner3.lasertag.lasertaggame.management.settings.SettingDataType.LONG;

/**
 * Callback for the client trigger setting change network event
 *
 * @author Étienne Muser
 */
public class ClientTriggerSettingChangeCallback implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
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
            LasertagGameManager.getInstance().getSettingsManager().set(server, settingDescription.getName(), settingValue);
        }
    }
}

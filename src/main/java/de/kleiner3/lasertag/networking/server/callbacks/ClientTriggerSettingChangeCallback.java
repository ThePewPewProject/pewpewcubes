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
        switch (settingDescription.getDataType()) {
            case BOOL -> settingValue = buf.readBoolean();
            case LONG -> settingValue = buf.readLong();
            default -> LasertagMod.LOGGER.error("ClientTriggerSettingChangeCallback: Unrecognised setting datatype '{}'", settingDescription.getDataType());
        }

        if (settingValue != null) {
            LasertagGameManager.getInstance().getSettingsManager().set(server, settingDescription.getName(), settingValue);
        }
    }
}

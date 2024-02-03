package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsPresetsNameManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.ISettingsPresetsNamesState;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Implementation of the ISettingsPresetsNameManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class SettingsPresetsNameManager implements ISettingsPresetsNameManager {

    private final MinecraftServer server;
    private final ISettingsPresetsNamesState settingsPresetsNamesState;

    public SettingsPresetsNameManager(ISettingsPresetsNamesState settingsPresetsNamesState, MinecraftServer server) {
        this.settingsPresetsNamesState = settingsPresetsNamesState;
        this.server = server;
    }

    @Override
    public void addPresetName(String name) {
        settingsPresetsNamesState.addPresetName(name);
        sendNetworkEvent(NetworkingConstants.SETTINGS_PRESET_ADDED, name);
    }

    @Override
    public void removePresetName(String name) {
        settingsPresetsNamesState.removePresetName(name);
        sendNetworkEvent(NetworkingConstants.SETTINGS_PRESET_REMOVED, name);
    }

    @Override
    public List<String> getSettingsPresetNames() {
        return settingsPresetsNamesState.getAllPresetNames();
    }

    private void sendNetworkEvent(Identifier networkEventId, String presetName) {

        var world = server.getOverworld();

        // Omit the event if the world has not loaded
        if (world == null) {
            return;
        }

        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeString(presetName);

        ServerEventSending.sendToEveryone(world, networkEventId, buf);
    }
}

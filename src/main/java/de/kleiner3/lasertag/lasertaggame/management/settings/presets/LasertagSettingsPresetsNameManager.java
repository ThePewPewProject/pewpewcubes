package de.kleiner3.lasertag.lasertaggame.management.settings.presets;

import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the preset names for the gui
 *
 * @author Étienne Muser
 */
public class LasertagSettingsPresetsNameManager implements IManager {

    private Set<String> settingsPresetNames;

    public LasertagSettingsPresetsNameManager() {
        settingsPresetNames = new HashSet<>();
    }

    public void addPresetName(MinecraftServer s, String name) {
        this.settingsPresetNames.add(name);

        if (s != null) {
            this.sendNetworkEvent(s, NetworkingConstants.SETTINGS_PRESET_ADDED, name);
        }
    }

    public void removePresetName(MinecraftServer s, String name) {
        this.settingsPresetNames.remove(name);

        if (s != null) {
            this.sendNetworkEvent(s, NetworkingConstants.SETTINGS_PRESET_REMOVED, name);
        }
    }

    public List<String> getSettingsPresetNames() {
        return this.settingsPresetNames.stream().toList();
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    private void sendNetworkEvent(MinecraftServer s, Identifier networkEventId, String presetName) {
        var buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeString(presetName);

        ServerEventSending.sendToEveryone(s.getOverworld(), networkEventId, buf);
    }
}

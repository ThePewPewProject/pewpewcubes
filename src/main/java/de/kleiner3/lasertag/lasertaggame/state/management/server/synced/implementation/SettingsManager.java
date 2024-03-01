package de.kleiner3.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.IGameModeManager;
import de.kleiner3.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.kleiner3.lasertag.lasertaggame.state.synced.implementation.SettingsState;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * Implementation of the ISettingsManager for the lasertag game
 *
 * @author Étienne Muser
 */
public class SettingsManager implements ISettingsManager {

    private final SettingsState settingsState;

    private final IGameModeManager gameModeManager;
    private final MinecraftServer server;

    // Get path to lasertag settings file
    private static final Path lasertagSettingsFilePath = LasertagMod.configFolderPath.resolve("lasertagSettings.json");

    public SettingsManager(MinecraftServer server, SettingsState settingsState, IGameModeManager gameModeManager) {

        this.server = server;
        this.settingsState = settingsState;
        this.gameModeManager = gameModeManager;

        if (Files.exists(lasertagSettingsFilePath)) {

            try {
                // Read settings file
                var settingsFileContents = Files.readString(lasertagSettingsFilePath);

                // Parse file
                var loadedSettingsState = SettingsState.fromJson(settingsFileContents);

                settingsState.putAll(loadedSettingsState);
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Reading of lasertag settings file failed: " + e.getMessage());
            }

        } else {

            // Use default settings
            var defaultSettingsState = gameModeManager.getGameMode().createDefaultSettings();

            settingsState.putAll(defaultSettingsState);

            // Log that default settings is being used
            LasertagMod.LOGGER.info("Default lasertag settings is being used.");

            // Write to settings file
            persist();
        }
    }

    @Override
    public <T> T get(SettingDescription setting) {
        var key = setting.getName();

        // If key not in settings
        if (!settingsState.containsKey(key)) {
            putDefault(key);
        }

        // Get value from dictionary
        return (T) settingsState.get(key);
    }

    @Override
    public <T extends Enum<T>> T getEnum(SettingDescription setting) {

        var key = setting.getName();

        // If key not in settings
        if (!settingsState.containsKey(key)) {
            putDefault(key);
        }

        // Get value from dictionary
        var value = settingsState.get(key);

        return Enum.valueOf((Class<T>) setting.getDataType().getValueType(), (String)value);
    }

    @Override
    public void set(String key, Object value) {

        // If is enum setting
        if (value instanceof Enum<?> enumValue) {
            value = enumValue.name();
        }

        settingsState.put(key, value);
        sync(key, value.toString());
    }

    @Override
    public void set(SettingsState newSettings) {

        settingsState.putAll(newSettings);
        sync();
    }

    @Override
    public void reset() {
        // Set the default settings
        settingsState.putAll(gameModeManager.getGameMode().createDefaultSettings());

        sync();
    }

    @Override
    public void reset(String settingName) {

        // Create default settings
        var defaultSettings = gameModeManager.getGameMode().createDefaultSettings();

        this.set(settingName, defaultSettings.get(settingName));

        sync(settingName, defaultSettings.get(settingName).toString());
    }

    @Override
    public SettingsState cloneSettings() {
        return SerializationUtils.clone(settingsState);
    }

    private void persist() {

        try {
            // Parse to json string
            var jsonString = settingsState.toJson();

            // Write to file
            Files.createDirectories(lasertagSettingsFilePath.getParent());
            Files.writeString(lasertagSettingsFilePath, jsonString, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            LasertagMod.LOGGER.warn("Failed to persist lasertag setting: " + e.getMessage());
        }
    }

    private void putDefault(String key) {
        // Get default settings
        var defaultSettings = gameModeManager.getGameMode().createDefaultSettings();

        // Get the default value
        var defaultValue = defaultSettings.get(key);

        // Put the default value in this settings
        settingsState.put(key, defaultValue);

        // Write to settings file
        persist();
    }

    /**
     * Persist the setting change to the file system and sends
     * a setting changed event to every client.
     *
     * @param key    The name of the setter method which executes the persist method
     * @param value  The new value of the setting as a string
     */
    private void sync(String key, String value) {

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write to packet
        buf.writeString(key);
        buf.writeString(value);

        // Send update to clients
        ServerEventSending.sendToEveryone(server, NetworkingConstants.SETTING_CHANGED, buf);

        // Persist in file
        persist();
    }

    /**
     * Persist the settings change to the file system and sends
     * a settings changed event to every client.
     */
    private void sync() {

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write to packet
        buf.writeString(settingsState.toJson());

        // Send update to clients
        ServerEventSending.sendToEveryone(server, NetworkingConstants.SETTINGS_CHANGED, buf);

        // Persist in file
        persist();
    }

    @Override
    public String toString() {

        var builder = new StringBuilder("--------------------\n");

        settingsState.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    builder.append(e.getKey());
                    builder.append(": ");
                    builder.append(e.getValue().toString());
                    builder.append("\n");
                });

        builder.append("--------------------");

        return builder.toString();
    }
}

package de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.implementation;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.IGameModeManager;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.synced.ISettingsManager;
import de.pewpewproject.lasertag.lasertaggame.state.server.implementation.SettingsPreset;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.SettingsState;
import de.pewpewproject.lasertag.networking.NetworkingConstants;
import de.pewpewproject.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

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
                
                settingsState.fillWith(loadedSettingsState);
            } catch (Exception e) {
                LasertagMod.LOGGER.warn("Reading of lasertag settings file failed: " + e.getMessage());
            }

        } else {

            // Log that default settings is being used
            LasertagMod.LOGGER.info("Default lasertag settings is being used.");

            // Write to settings file
            persist();
        }
    }

    @Override
    public synchronized <T> T get(SettingDescription setting) {
        var gameModeName = gameModeManager.getGameMode().getTranslatableName();
        var settingName = setting.getName();

        // If key not in settings
        if (!settingsState.contains(gameModeName, settingName)) {
            putDefault(settingName);
        }

        // Get value from dictionary
        return (T) settingsState.get(gameModeName, settingName);
    }

    @Override
    public synchronized <T extends Enum<T>> T getEnum(SettingDescription setting) {

        var gameModeName = gameModeManager.getGameMode().getTranslatableName();
        var settingName = setting.getName();

        // If key not in settings
        if (!settingsState.contains(gameModeName, settingName)) {
            putDefault(settingName);
        }

        // Get value from dictionary
        var value = settingsState.get(gameModeName, settingName);

        return Enum.valueOf((Class<T>) setting.getDataType().getValueType(), (String) value);
    }

    @Override
    public synchronized void set(String key, Object value) {

        var gameModeName = gameModeManager.getGameMode().getTranslatableName();

        // If is enum setting
        if (value instanceof Enum<?> enumValue) {
            value = enumValue.name();
        }

        settingsState.set(gameModeName, key, value);
        sync(key, value.toString());
    }

    @Override
    public void set(SettingsPreset preset) {

        var gameModeName = gameModeManager.getGameMode().getTranslatableName();

        preset.forEach((sn, sv) -> settingsState.set(gameModeName, sn, sv));

        sync();
    }

    @Override
    public synchronized void set(SettingsState newSettings) {

        settingsState.fillWith(newSettings);
        sync();
    }

    @Override
    public synchronized void reset() {

        // Set the default settings
        settingsState.fillWith(new SettingsState());

        sync();
    }

    @Override
    public synchronized void reset(String settingName) {

        var gameModeName = gameModeManager.getGameMode().getTranslatableName();

        // Create default settings
        var defaultSettings = new SettingsState();

        this.set(settingName, defaultSettings.get(gameModeName, settingName));

        sync(settingName, defaultSettings.get(gameModeName, settingName).toString());
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

        var gameModeName = gameModeManager.getGameMode().getTranslatableName();

        // Get default settings
        var defaultSettings = new SettingsState();

        // Get the default value
        var defaultValue = defaultSettings.get(gameModeName, key);

        // Put the default value in this settings
        settingsState.set(gameModeName, key, defaultValue);

        // Write to settings file
        persist();
    }

    /**
     * Persist the setting change to the file system and sends
     * a setting changed event to every client.
     *
     * @param key   The name of the setter method which executes the persist method
     * @param value The new value of the setting as a string
     */
    private void sync(String key, String value) {

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write game mode to packet
        buf.writeString(gameModeManager.getGameMode().getTranslatableName());

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

        // Write game mode to packet
        buf.writeString(gameModeManager.getGameMode().getTranslatableName());

        // Write to packet
        buf.writeString(settingsState.toJson());

        // Send update to clients
        ServerEventSending.sendToEveryone(server, NetworkingConstants.SETTINGS_CHANGED, buf);

        // Persist in file
        persist();
    }

    @Override
    public String toString() {

        var gameModeName = gameModeManager.getGameMode().getTranslatableName();

        var builder = new StringBuilder(I18n.translate(gameModeName));
        builder.append(":\n--------------------\n");

        Arrays.stream(SettingDescription.values())
                .map(SettingDescription::getName)
                .sorted()
                .forEach(s -> {
                    builder.append(s);
                    builder.append(": ");
                    builder.append(settingsState.get(gameModeName, s).toString());
                    builder.append("\n");
                });

        builder.append("--------------------");

        return builder.toString();
    }
}

package de.kleiner3.lasertag.lasertaggame.management.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.GameMode;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Manages the lasertag settings
 *
 * @author Étienne Muser
 */
public class LasertagSettingsManager implements IManager {
    //region Private fields
    private LasertagSettingsMap settings;

    // Get path to lasertag settings file
    private static final Path lasertagSettingsFilePath = LasertagMod.configFolderPath.resolve("lasertagSettings.json");


    //endregion

    public LasertagSettingsManager(GameMode gameMode) {

        if (Files.exists(lasertagSettingsFilePath)) {

            try {
                // Read settings file
                var settingsFileContents = Files.readString(lasertagSettingsFilePath);

                // Parse file
                settings = LasertagSettingsMap.fromJson(settingsFileContents);
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Reading of lasertag settings file failed: " + e.getMessage());
            }

        } else {

            // Use default settings
            settings = gameMode.createDefaultSettings();

            // Log that default settings is being used
            LasertagMod.LOGGER.info("Default lasertag settings is being used.");

            // Write to settings file
            persist();
        }
    }

    //region Public methods

    /**
     * Get the value of the setting. Only for non-enum values.
     *
     * @param setting The setting description of the setting
     * @param <T>     The type of the value of the setting
     * @return The value of the setting
     */
    public <T> T get(SettingDescription setting) {

        var key = setting.getName();

        // If key not in settings
        if (!settings.containsKey(key)) {
            putDefault(key);
        }

        // Get value from dictionary
        return (T) settings.get(key);
    }

    /**
     * Get the enum value of a setting. Only for enum values.
     *
     * @param setting The setting description of the setting
     * @param <T>     The type of the value of the setting
     * @return The value of the setting
     */
    public <T extends Enum<T>> T getEnum(SettingDescription setting) {
        var key = setting.getName();

        // If key not in settings
        if (!settings.containsKey(key)) {
            putDefault(key);
        }

        // Get value from dictionary
        var value = settings.get(key);

        return Enum.valueOf((Class<T>) setting.getDataType().getValueType(), (String) value);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    public static LasertagSettingsManager fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, LasertagSettingsManager.class);
    }

    public String toJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(settings);
    }

    public void set(MinecraftServer s, String key, Object value) {

        // If is enum setting
        if (value instanceof Enum<?> enumValue) {
            value = enumValue.name();
        }

        settings.put(key, value);
        sync(s, key, value.toString());
    }

    public void set(MinecraftServer s, LasertagSettingsMap newSettings) {
        settings = newSettings;
        sync(s);
    }

    public void set(String newSettingsJson) {
        settings = LasertagSettingsMap.fromJson(newSettingsJson);
    }

    public void reset(MinecraftServer s) {
        // Set the default settings
        settings = LasertagGameManager.getInstance().getGameModeManager().getGameMode().createDefaultSettings();

        sync(s);
    }

    public void reset(MinecraftServer s, String settingName) {
        // Create default settings
        var defaultSettings = LasertagGameManager.getInstance().getGameModeManager().getGameMode().createDefaultSettings();

        this.set(s, settingName, defaultSettings.get(settingName));
    }

    public LasertagSettingsMap getSettingsClone() {
        return new LasertagSettingsMap(settings);
    }

    //endregion

    private void putDefault(String key) {
        // Get default settings
        var defaultSettings = LasertagGameManager.getInstance().getGameModeManager().getGameMode().createDefaultSettings();

        // Get the default value
        var defaultValue = defaultSettings.get(key);

        // Put the default value in this settings
        settings.put(key, defaultValue);

        // Write to settings file
        persist();
    }

    //region Persistence

    /**
     * Persist the setting change to the file system and sends
     * a setting changed event to every client.
     *
     * @param server The server this is executed on. null if on the client
     * @param key    The name of the setter method which executes the persist method
     * @param value  The new value of the setting as a string
     */
    private void sync(MinecraftServer server, String key, String value) {
        // Check if this is executed on client
        if (server == null) {
            // Do not persist lasertag setting on client
            return;
        }

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write to packet
        buf.writeString(key);
        buf.writeString(value);

        // Send update to clients
        ServerEventSending.sendToEveryone(server.getOverworld(), NetworkingConstants.SETTING_CHANGED, buf);

        // Persist in file
        persist();
    }

    /**
     * Persist the settings change to the file system and sends
     * a settings changed event to every client.
     *
     * @param server The server this is executed on. null if on the client
     */
    private void sync(MinecraftServer server) {

        // Check if this is executed on client
        if (server == null) {
            // Do not persist lasertag setting on client
            return;
        }

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write to packet
        buf.writeString(settings.toJson());

        // Send update to clients
        ServerEventSending.sendToEveryone(server.getOverworld(), NetworkingConstants.SETTINGS_CHANGED, buf);

        // Persist in file
        persist();
    }

    private void persist() {

        try {
            // Parse to json string
            var jsonString = settings.toJson();

            // Write to file
            Files.createDirectories(lasertagSettingsFilePath.getParent());
            Files.writeString(lasertagSettingsFilePath, jsonString, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            LasertagMod.LOGGER.warn("Failed to persist lasertag setting: " + e.getMessage());
        }
    }

    //endregion
}

package de.kleiner3.lasertag.lasertaggame.management.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.FileIO;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;

/**
 * Manages the lasertag settings
 *
 * @author Étienne Muser
 */
public class LasertagSettingsManager implements IManager {
    //region Private fields
    private LasertagSettingsMap settings;

    // Get path to lasertag settings file
    private static final String lasertagSettingsFilePath = LasertagMod.configFolderPath + "\\lasertagSettings.json";

    // Create file object
    private static final File lasertagSettingsFile = new File(lasertagSettingsFilePath);

    //endregion

    public LasertagSettingsManager() {
        if (lasertagSettingsFile.exists()) {

            try {
                // Read settings file
                var settingsFileContents = FileIO.readAllFile(lasertagSettingsFile);

                // Parse file
                settings = LasertagSettingsMap.fromJson(settingsFileContents);
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Reading of lasertag settings file failed: " + e.getMessage());
            }

        } else {

            try {
                // Use default settings
                settings = LasertagSettingsMap.createDefaultSettings();

                // Log that default settings is being used
                LasertagMod.LOGGER.info("Default lasertag settings is being used.");

                // Create settings file
                var ignored = FileIO.createNewFile(lasertagSettingsFilePath);

                // Write to settings file
                persistUnsafe();
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Creation of new lasertag settings file in '" + lasertagSettingsFilePath + "' failed: " + e.getMessage());
            }
        }
    }

    //region Public methods

    public <T> T get(SettingDescription setting) {

        var key = setting.getName();

        // If key not in settings
        if (!settings.containsKey(key)) {
            putDefault(key);
        }

        return (T)settings.get(key);
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
        settings.put(key, value);
        persist(s, key, value.toString());
    }

    public void set(String newSettingsJson) {
        settings = LasertagSettingsMap.fromJson(newSettingsJson);
    }

    public void reset() {
        // Set the default settings
        settings = LasertagSettingsMap.createDefaultSettings();
    }

    //endregion

    private void putDefault(String key) {
        // Get default settings
        var defaultSettings = LasertagSettingsMap.createDefaultSettings();

        // Get the default value
        var defaultValue = defaultSettings.get(key);

        // Put the default value in this settings
        settings.put(key, defaultValue);

        try {
            // Write to settings file
            persistUnsafe();
        } catch (IOException ex) {
            LasertagMod.LOGGER.warn("Persisting of lasertag settings file in '" + lasertagSettingsFilePath + "' failed: " + ex.getMessage());
        }
    }

    //region Persistence

    /**
     * Persist the setting changes to the file system.
     *
     * @param server The server this is executed on. null if on the client
     * @param key    The name of the setter method which executes the persist method
     * @param value  The new value of the setting as a string
     */
    private void persist(MinecraftServer server, String key, String value) {
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
        ServerEventSending.sendToEveryone(server.getOverworld(), NetworkingConstants.SETTINGS_CHANGED, buf);

        try {
            persistUnsafe();
        } catch (IOException e) {
            LasertagMod.LOGGER.warn("Failed to persist lasertag setting: " + e.getMessage());
        }
    }

    private void persistUnsafe() throws IOException {
        // Parse to json string
        var jsonString = settings.toJson();

        // Write to file
        FileIO.writeAllFile(lasertagSettingsFile, jsonString);
    }

    //endregion
}

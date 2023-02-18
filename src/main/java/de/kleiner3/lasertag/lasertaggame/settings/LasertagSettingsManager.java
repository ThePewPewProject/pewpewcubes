package de.kleiner3.lasertag.lasertaggame.settings;

import com.google.gson.GsonBuilder;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.common.util.FileIO;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.IOException;

/**
 * Manages the lasertag config
 *
 * @author Étienne Muser
 */
public class LasertagSettingsManager {
    //region Static fields
    private static LasertagSettingsMap settings;

    // Get path to lasertag config file
    private static final String lasertagConfigFilePath = LasertagMod.configFolderPath + "\\lasertagConfig.json";

    // Create file object
    private static final File lasertagConfigFile = new File(lasertagConfigFilePath);

    //endregion

    public static Object get(String key) {
        return settings.get(key);
    }

    public static String get() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(settings);
    }

    public static void set(MinecraftServer s, String key, Object value) {
        settings.put(key, value);
        persist(s, key, value.toString());
    }

    public static void set(String newSettingsJson) {
        settings = LasertagSettingsMap.fromJson(newSettingsJson);
    }

    public static void syncToPlayer(ServerPlayerEntity player) {
        // Serialize to json
        var json = settings.toJson();

        // Create packet buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Write errorMessage to buffer
        buf.writeString(json);

        // Send to all clients
        ServerPlayNetworking.send(player, NetworkingConstants.LASERTAG_SETTINGS_SYNC, buf);
    }

    //region Persistence

    /**
     * Persist the config changes to the file system.
     *
     * @param server The server this is executed on. null if on the client
     * @param key    The name of the setter method which executes the persist method
     * @param value  The new value of the setting as a string
     */
    private static void persist(MinecraftServer server, String key, String value) {
        // Check if this is executed on client
        if (server == null) {
            // Do not persist lasertag config on client
            return;
        }

        // Create packet
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Write to packet
        buf.writeString(key);
        buf.writeString(value);

        // Send update to clients
        ServerEventSending.sendToEveryone(server.getOverworld(), NetworkingConstants.LASERTAG_SETTINGS_CHANGED, buf);

        try {
            persistUnsafe();
        } catch (IOException e) {
            LasertagMod.LOGGER.warn("Failed to persist lasertag config: " + e.getMessage());
        }
    }

    private static void persistUnsafe() throws IOException {
        // Parse to json string
        var jsonString = settings.toJson();

        // Write to file
        FileIO.writeAllFile(lasertagConfigFile, jsonString);
    }

    // Initialize lasertag game settings from file
    static {
        if (lasertagConfigFile.exists()) {

            try {
                // Read config file
                var configFileContents = FileIO.readAllFile(lasertagConfigFile);

                // Parse file
                settings = LasertagSettingsMap.fromJson(configFileContents);
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Reading of lasertag config file failed: " + e.getMessage());
            }

        } else {

            try {
                // Use default config
                settings = LasertagSettingsMap.createDefaultSettings();

                // Log that default config is being used
                LasertagMod.LOGGER.info("Default lasertag config is being used.");

                // Create config file
                var ignored = FileIO.createNewFile(lasertagConfigFilePath);

                // Write to config file
                persistUnsafe();
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Creation of new lasertag config file in '" + lasertagConfigFilePath + "' failed: " + e.getMessage());
            }
        }
    }

    //endregion
}

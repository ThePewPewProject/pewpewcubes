package de.kleiner3.lasertag;

import com.google.gson.Gson;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import de.kleiner3.lasertag.util.FileIO;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.IOException;

/**
 * All configuration variables for lasertag.
 *
 * @author Étienne Muser
 */
public class LasertagConfig {
    // ===== Weapon settings ====================
    /**
     * Weapon cooldown in game ticks
     */
    private int lasertagWeaponCooldown = 5;
    private int lasertagWeaponReach = 50;
    private boolean showLaserRays = true;

    // ===== General game settings ==============
    private int maxTeamSize = 6;
    private boolean renderTeamList = true;
    private boolean renderTimer = true;
    private int lasertargetHitScore = 100;
    private int playerHitScore = 20;

    /**
     * The time in seconds from when you are teleported into the arena to when the game actually starts in seconds
     */
    private int startTime = 15;
    /**
     * The time the player is deactivated after being hit in seconds
     */
    private int deactivateTime = 15;

    /**
     * The time in seconds a lasertarget will be deactivated for after being hit
     */
    private int lasertargetDeactivatedTime = 10;

    /**
     * The play time in minutes
     */
    private int playTime = 10;

    public int getLasertagWeaponCooldown() {
        return lasertagWeaponCooldown;
    }

    public void setLasertagWeaponCooldown(MinecraftServer s, Integer lasertagWeaponCooldown) {
        this.lasertagWeaponCooldown = lasertagWeaponCooldown;
        persist(s, "setLasertagWeaponCooldown", Integer.toString(lasertagWeaponCooldown));
    }

    public int getLasertagWeaponReach() {
        return lasertagWeaponReach;
    }

    public void setLasertagWeaponReach(MinecraftServer s, Integer lasertagWeaponReach) {
        this.lasertagWeaponReach = lasertagWeaponReach;
        persist(s, "setLasertagWeaponReach", Integer.toString(lasertagWeaponReach));
    }

    public boolean isShowLaserRays() {
        return showLaserRays;
    }

    public void setShowLaserRays(MinecraftServer s, Boolean showLaserRays) {
        this.showLaserRays = showLaserRays;
        persist(s, "setShowLaserRays", Boolean.toString(showLaserRays));
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public void setMaxTeamSize(MinecraftServer s, Integer maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
        persist(s, "setMaxTeamSize", Integer.toString(maxTeamSize));
    }

    public boolean isRenderTeamList() {
        return renderTeamList;
    }

    public void setRenderTeamList(MinecraftServer s, Boolean renderTeamList) {
        this.renderTeamList = renderTeamList;
        persist(s, "setRenderTeamList", Boolean.toString(renderTeamList));
    }

    public boolean isRenderTimer() {
        return renderTimer;
    }

    public void setRenderTimer(MinecraftServer s, Boolean renderTimer) {
        this.renderTimer = renderTimer;
        persist(s, "setRenderTimer", Boolean.toString(renderTimer));
    }

    public int getLasertargetHitScore() {
        return lasertargetHitScore;
    }

    public void setLasertargetHitScore(MinecraftServer s, Integer lasertargetHitScore) {
        this.lasertargetHitScore = lasertargetHitScore;
        persist(s, "setLasertargetHitScore", Integer.toString(lasertargetHitScore));
    }

    public int getPlayerHitScore() {
        return playerHitScore;
    }

    public void setPlayerHitScore(MinecraftServer s, Integer playerHitScore) {
        this.playerHitScore = playerHitScore;
        persist(s, "setPlayerHitScore", Integer.toString(playerHitScore));
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(MinecraftServer s, Integer startTime) {
        this.startTime = startTime;
        persist(s, "setStartTime", Integer.toString(startTime));
    }

    public int getDeactivateTime() {
        return deactivateTime;
    }

    public void setDeactivateTime(MinecraftServer s, Integer deactivateTime) {
        this.deactivateTime = deactivateTime;
        persist(s, "setDeactivateTime", Integer.toString(deactivateTime));
    }

    public int getLasertargetDeactivatedTime() {
        return lasertargetDeactivatedTime;
    }

    public void setLasertargetDeactivatedTime(MinecraftServer s, Integer lasertargetDeactivatedTime) {
        this.lasertargetDeactivatedTime = lasertargetDeactivatedTime;
        persist(s, "setLasertargetDeactivatedTime", Integer.toString(lasertargetDeactivatedTime));
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(MinecraftServer s, Integer playTime) {
        this.playTime = playTime;
        persist(s, "setPlayTime", Integer.toString(playTime));
    }

    public static LasertagConfig getInstance() {
        return instance;
    }

    public static void setInstance(LasertagConfig inst) {
        instance = inst;
    }

    public static void syncToPlayer(ServerPlayerEntity player) {
        // Serialize to json
        var json = new Gson().toJson(instance);

        // Create packet buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Write errorMessage to buffer
        buf.writeString(json);

        // Send to all clients
        ServerPlayNetworking.send(player, NetworkingConstants.LASERTAG_SETTINGS_SYNC, buf);
    }

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
        var jsonString = new Gson().toJson(instance);

        // Write to file
        FileIO.writeAllFile(lasertagConfigFile, jsonString);
    }

    // Get path to lasertag config file
    private static String lasertagConfigFilePath = LasertagMod.configFolderPath + "\\lasertagConfig.json";

    // Create file object
    private static File lasertagConfigFile = new File(lasertagConfigFilePath);

    private static LasertagConfig instance = null;

    private LasertagConfig() {
    }

    /**
     * Initialize lasertag game settings from file
     */
    static {
        try {
            // Read config file
            var configFileContents = FileIO.readAllFile(lasertagConfigFile);

            // Parse file
            instance = new Gson().fromJson(configFileContents, LasertagConfig.class);
        } catch (IOException ioex) {
            LasertagMod.LOGGER.warn("Reading of lasertag config file failed: " + ioex.getMessage());
        } catch (Exception ex) {
            LasertagMod.LOGGER.warn("Setting of lasertag config failed: " + ex.getMessage());
        }

        // If instance wasn't created
        if (instance == null) {
            // Use default config
            instance = new LasertagConfig();

            // Log that default config is being used
            LasertagMod.LOGGER.info("Default lasertag config is being used.");
        }

        // Create directory if not exists
        var dir = new File(LasertagMod.configFolderPath);
        if (dir.exists() == false) {
            dir.mkdir();
        }

        // If config file doesn't exist
        if (lasertagConfigFile.exists() == false) {
            // Log that lasertag config file is being created
            LasertagMod.LOGGER.info("Lasertag config file is being created in '" + lasertagConfigFilePath + "'");

            try {
                // Create new file
                lasertagConfigFile.createNewFile();

                persistUnsafe();
            } catch (IOException e) {
                // Log that creation of new file failed
                LasertagMod.LOGGER.warn("Creation of new lasertag config file in '" + lasertagConfigFilePath + "' failed: " + e.getMessage());
            }
        }
    }
}

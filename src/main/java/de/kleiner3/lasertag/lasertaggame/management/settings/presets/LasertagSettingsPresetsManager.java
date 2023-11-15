package de.kleiner3.lasertag.lasertaggame.management.settings.presets;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Manages the lasertag settings presets
 *
 * @author Étienne Muser
 */
public class LasertagSettingsPresetsManager {
    //region Private fields
    private LasertagSettingsPresetsMap presets;

    // Get path to lasertag settings file
    private static final Path lasertagSettingsPresetsFilePath = LasertagMod.configFolderPath.resolve("lasertagSettingsPresets.json");

    //endregion

    public LasertagSettingsPresetsManager() {

        if (Files.exists(lasertagSettingsPresetsFilePath)) {

            try {
                var presetsFileContents = Files.readString(lasertagSettingsPresetsFilePath);

                presets = LasertagSettingsPresetsMap.fromJson(presetsFileContents);
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Reading of lasertag settings presets file failed: " + e.getMessage());
            }
        } else {
            presets = LasertagSettingsPresetsMap.createNewPresetsMap();
            persist();
        }

        if (presets != null) {
            presets.keySet().forEach(s -> LasertagGameManager.getInstance().getPresetsNameManager().addPresetName(null, s));
        }
    }

    public LasertagSettingsPresetsMap getPresets() {
        return this.presets;
    }

    public void savePreset(String name) {
        this.presets.put(name, LasertagGameManager.getInstance().getSettingsManager().getSettingsClone());
        persist();
    }

    public boolean loadPreset(String name, MinecraftServer server) {

        if (!presets.containsKey(name)) {
            return false;
        }

        var preset = presets.get(name);
        LasertagGameManager.getInstance().getSettingsManager().set(server, preset);
        return true;
    }

    public boolean deletePreset(String name) {

        if (!presets.containsKey(name)) {
            return false;
        }

        presets.remove(name);
        persist();
        return true;
    }

    private void persist() {
        try {
            Files.createDirectories(lasertagSettingsPresetsFilePath.getParent());
            Files.writeString(lasertagSettingsPresetsFilePath, presets.toJson(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException ex) {
            LasertagMod.LOGGER.error("Could not persist preset: " + ex.getMessage());
        }
    }
}

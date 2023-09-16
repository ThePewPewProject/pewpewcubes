package de.kleiner3.lasertag.lasertaggame.management.settings.presets;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.FileIO;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;

/**
 * Manages the lasertag settings presets
 *
 * @author Étienne Muser
 */
public class LasertagSettingsPresetsManager {
    //region Private fields
    private LasertagSettingsPresetsMap presets;

    // Get path to lasertag settings file
    private static final String lasertagSettingsPresetsFilePath = LasertagMod.configFolderPath + "\\lasertagSettingsPresets.json";

    // Create file object
    private static final File lasertagSettingsPresetsFile = new File(lasertagSettingsPresetsFilePath);

    //endregion

    public LasertagSettingsPresetsManager() {
        if (lasertagSettingsPresetsFile.exists()) {
            try {
                var presetsFileContents = FileIO.readAllFile(lasertagSettingsPresetsFile);

                presets = LasertagSettingsPresetsMap.fromJson(presetsFileContents);
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Reading of lasertag settings presets file failed: " + e.getMessage());
            }
        } else {
            try {
                presets = LasertagSettingsPresetsMap.createNewPresetsMap();

                var ignored = FileIO.createNewFile(lasertagSettingsPresetsFilePath);

                persistUnsafe();
            } catch (IOException e) {
                LasertagMod.LOGGER.warn("Creation of new lasertag settings presets file in '" + lasertagSettingsPresetsFilePath + "' failed: " + e.getMessage());
            }
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
            this.persistUnsafe();
        } catch (IOException ex) {
            LasertagMod.LOGGER.error("Could not persist preset: " + ex.getMessage());
        }
    }

    private void persistUnsafe() throws IOException {
        var json = presets.toJson();

        FileIO.writeAllFile(lasertagSettingsPresetsFile, json);
    }
}

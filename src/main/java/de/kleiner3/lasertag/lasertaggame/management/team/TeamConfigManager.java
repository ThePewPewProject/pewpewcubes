package de.kleiner3.lasertag.lasertaggame.management.team;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamConfigManagerDeserializer;
import de.kleiner3.lasertag.common.util.FileIO;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamDtoSerializer;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class to manage the lasertag teams
 *
 * @author Étienne Muser
 */
public class TeamConfigManager implements IManager {

    public static final TeamDto SPECTATORS = new TeamDto("Spectators", 128, 128, 128, null);

    /**
     * The path to the team config file
     */
    private static final String teamConfigFilePath = LasertagMod.configFolderPath + "\\teamConfig.json";

    /**
     * The actual in-memory team config
     */
    public HashMap<String, TeamDto> teamConfig = null;

    public TeamConfigManager() {
        // Get config file
        var teamConfigFile = new File(teamConfigFilePath);

        // If the config file exists
        if (teamConfigFile.exists()) {
            try {
                // Read config file
                var configFileContents = FileIO.readAllFile(teamConfigFile);

                // get gson builder
                var gsonBuilder = new GsonBuilder();

                // Get deserializer
                var deserializer = TeamConfigManagerDeserializer.getDeserializer();

                // Register type
                gsonBuilder.registerTypeAdapter(HashMap.class, deserializer);

                // Parse
                teamConfig = gsonBuilder.create().fromJson(configFileContents, new TypeToken<HashMap<String, TeamDto>>() {
                }.getType());
            } catch (IOException ex) {
                LasertagMod.LOGGER.warn("Reading of team config file failed: " + ex.getMessage());
            }
        }

        // If config couldn't be loaded from file
        if (teamConfig == null) {
            // Create map
            teamConfig = new HashMap<>();

            // Fill map with default values
            teamConfig.put("Red", new TeamDto("Red", 255, 0, 0, Blocks.RED_CONCRETE));
            teamConfig.put("Green", new TeamDto("Green", 0, 255, 0, Blocks.LIME_CONCRETE));
            teamConfig.put("Blue", new TeamDto("Blue", 0, 0, 255, Blocks.BLUE_CONCRETE));
            teamConfig.put("Orange", new TeamDto("Orange", 255, 128, 0, Blocks.ORANGE_CONCRETE));
            teamConfig.put("Teal", new TeamDto("Teal", 0, 128, 255, Blocks.LIGHT_BLUE_CONCRETE));
            teamConfig.put("Pink", new TeamDto("Pink", 255, 0, 255, Blocks.PINK_CONCRETE));

            // Get gson builder
            var gsonBuilder = new GsonBuilder();

            // Register type adapter
            gsonBuilder.registerTypeAdapter(TeamDto.class, TeamDtoSerializer.getSerializer());

            // Serialize
            var configJson = gsonBuilder.setPrettyPrinting().create().toJson(teamConfig);

            // Persist
            try {
                var dir = new File(LasertagMod.configFolderPath);

                // Create directory if not exists
                if (!dir.exists() && !dir.mkdir()) {
                    throw new IOException("Make directory for team config failed!");
                }

                // Create file if not exists
                if (!teamConfigFile.exists() && !teamConfigFile.createNewFile()) {
                    throw new IOException("Creation of file for team config failed!");
                }

                // Write to file
                FileIO.writeAllFile(teamConfigFile, configJson);
            } catch (IOException e) {
                LasertagMod.LOGGER.error("Writing to team config file failed: " + e.getMessage());
            }
        }

        // Add dummy team spectators
        teamConfig.put(SPECTATORS.name(), SPECTATORS);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static TeamConfigManager fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, TeamConfigManager.class);
    }

    @Override
    public void syncToClient(ServerPlayerEntity client, MinecraftServer server) {
        // Do not sync!
        throw new UnsupportedOperationException("TeamConfigManager should not be synced on its own.");
    }
}

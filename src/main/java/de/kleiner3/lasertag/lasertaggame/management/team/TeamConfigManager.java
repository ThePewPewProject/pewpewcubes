package de.kleiner3.lasertag.lasertaggame.management.team;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.common.util.FileIO;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamConfigManagerDeserializer;
import de.kleiner3.lasertag.lasertaggame.management.team.serialize.TeamDtoSerializer;
import net.minecraft.block.Blocks;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

/**
 * Class holding the team config information
 *
 * @author Étienne Muser
 */
public class TeamConfigManager {

    private static final String teamConfigFilePath = LasertagMod.configFolderPath + "\\teamConfig.json";

    public static final TeamDto SPECTATORS = new TeamDto(0, "Spectators", 128, 128, 128, null);
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
            } catch (Exception ex) {
                LasertagMod.LOGGER.error("Unknown exception during loading of team config file: " + ex.getMessage());
            }
        }

        // If config couldn't be loaded from file
        if (teamConfig == null) {

            LasertagMod.LOGGER.info("Using default team config...");

            // Create map
            teamConfig = new HashMap<>();

            // Fill map with default values
            teamConfig.put("Red", new TeamDto(1, "Red", 255, 0, 0, Blocks.RED_CONCRETE));
            teamConfig.put("Green", new TeamDto(2, "Green", 0, 255, 0, Blocks.LIME_CONCRETE));
            teamConfig.put("Blue", new TeamDto(3, "Blue", 0, 0, 255, Blocks.BLUE_CONCRETE));
            teamConfig.put("Orange", new TeamDto(4,"Orange", 255, 128, 0, Blocks.ORANGE_CONCRETE));
            teamConfig.put("Teal", new TeamDto(5, "Teal", 0, 128, 255, Blocks.LIGHT_BLUE_CONCRETE));
            teamConfig.put("Pink", new TeamDto(6, "Pink", 255, 0, 255, Blocks.PINK_CONCRETE));

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

    /**
     * Gets the team identified by its id
     *
     * @param id The id of the team
     * @return
     */
    public Optional<TeamDto> getTeamOfId(int id) {

        return teamConfig.values().stream()
                .filter(team -> team.id() == id)
                .findFirst();
    }
}

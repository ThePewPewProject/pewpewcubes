package de.pewpewproject.lasertag.lasertaggame.state.synced.implementation;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.lasertaggame.state.synced.ITeamsConfigState;
import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;
import de.pewpewproject.lasertag.lasertaggame.team.serialize.TeamConfigManagerDeserializer;
import de.pewpewproject.lasertag.lasertaggame.team.serialize.TeamDtoSerializer;
import net.minecraft.block.Blocks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ITeamsConfigState for the lasertag game.
 *
 * @author Étienne Muser
 */
public class TeamsConfigState implements ITeamsConfigState {

    /**
     * The path to the team config file
     */
    private static final Path teamConfigFilePath = LasertagMod.configFolderPath.resolve("teamConfig.json");

    /**
     * The static instance of the dummy team "Spectators"
     */
    public static final TeamDto SPECTATORS = new TeamDto(0, "Spectators", 128, 128, 128, null);

    /**
     * The team config
     *     key: The name of the team
     *     value: The TeamDto
     */
    public HashMap<String, TeamDto> teamConfig;

    public TeamsConfigState() {
        reload();
    }

    //region Public methods

    /**
     * Resets the team config manager to its default state
     */
    public synchronized void reset() {

        // Clear the old config
        teamConfig.clear();

        // Create the new config
        var newConfig = createDefaultConfig();

        // Set the new config
        teamConfig.putAll(newConfig);

        this.persist();
    }

    @Override
    public synchronized void reload() {

        // If the config file exists
        if (Files.exists(teamConfigFilePath)) {
            try {
                // Read config file
                var configFileContents = Files.readString(teamConfigFilePath);

                teamConfig = deserializeTeamConfig(configFileContents);
            } catch (IOException ex) {
                LasertagMod.LOGGER.warn("Reading of team config file failed: " + ex.getMessage());
            }
        } else {

            // Log
            LasertagMod.LOGGER.info("Using default team config...");

            // Load default config
            teamConfig = createDefaultConfig();

            // Persist
            this.persist();
        }
    }

    /**
     * Gets the team identified by its id
     *
     * @param id The id of the team
     * @return
     */
    public synchronized Optional<TeamDto> getTeamOfId(int id) {

        return teamConfig.values().stream()
                .filter(team -> team.id() == id)
                .findFirst();
    }

    /**
     * Gets the team identified by its name
     *
     * @param name The name of the team to find
     * @return
     */
    public synchronized Optional<TeamDto> getTeamOfName(String name) {

        return teamConfig.values().stream()
                .filter(team -> team.name().equals(name))
                .findFirst();
    }

    @Override
    public synchronized List<TeamDto> getTeams() {
        return teamConfig.values().stream().toList();
    }

    @Override
    public synchronized void setTeamConfig(String jsonString) {

        // Clear the old config
        teamConfig.clear();;

        // Deserialize the new config
        var newConfig = deserializeTeamConfig(jsonString);

        // Set the new config
        teamConfig.putAll(newConfig);
    }

    public synchronized String toJson() {

        // Copy team config
        var teamConfigCopy = new HashMap<>(teamConfig);

        // Remove spectators (Do not persist spectators)
        teamConfigCopy.remove(SPECTATORS.name());

        // Get gson builder
        var gsonBuilder = new GsonBuilder();

        // Register type adapter
        gsonBuilder.registerTypeAdapter(TeamDto.class, TeamDtoSerializer.getSerializer());

        // Serialize
        return gsonBuilder.setPrettyPrinting().create().toJson(teamConfigCopy);
    }

    //endregion

    //region Private methods

    private static HashMap<String, TeamDto> deserializeTeamConfig(String json) {

        // get gson builder
        var gsonBuilder = new GsonBuilder();

        // Get deserializer
        var deserializer = TeamConfigManagerDeserializer.getDeserializer();

        // Register type
        gsonBuilder.registerTypeAdapter(HashMap.class, deserializer);

        // Parse
        HashMap<String, TeamDto> teamConfig = gsonBuilder.create().fromJson(json, new TypeToken<HashMap<String, TeamDto>>() {}.getType());

        // Add dummy team spectators
        teamConfig.put(SPECTATORS.name(), SPECTATORS);

        return teamConfig;
    }

    private static HashMap<String, TeamDto> createDefaultConfig() {

        // Create map
        var teamConfig = new HashMap<String, TeamDto>();

        // Fill map with default values
        teamConfig.put("Red", new TeamDto(1, "Red", 255, 0, 0, Blocks.RED_CONCRETE));
        teamConfig.put("Green", new TeamDto(2, "Green", 0, 255, 0, Blocks.LIME_CONCRETE));
        teamConfig.put("Blue", new TeamDto(3, "Blue", 0, 0, 255, Blocks.BLUE_CONCRETE));
        teamConfig.put("Orange", new TeamDto(4,"Orange", 255, 128, 0, Blocks.ORANGE_CONCRETE));
        teamConfig.put("Teal", new TeamDto(5, "Teal", 0, 128, 255, Blocks.LIGHT_BLUE_CONCRETE));
        teamConfig.put("Pink", new TeamDto(6, "Pink", 255, 0, 255, Blocks.PINK_CONCRETE));

        // Add dummy team spectators
        teamConfig.put(SPECTATORS.name(), SPECTATORS);

        return teamConfig;
    }

    private void persist() {

        // Serialize
        var configJson = this.toJson();

        try {
            // Write to file
            Files.createDirectories(teamConfigFilePath.getParent());
            Files.writeString(teamConfigFilePath, configJson, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            LasertagMod.LOGGER.error("Writing to team config file failed: " + e.getMessage());
        }
    }

    //endregion
}

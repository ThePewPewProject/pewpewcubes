package de.kleiner3.lasertag.types;

import com.google.gson.reflect.TypeToken;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.util.FileIO;
import de.kleiner3.lasertag.util.serialize.TeamConfigManagerDeserializer;
import de.kleiner3.lasertag.util.serialize.TeamDtoSerializer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class to manage the lasertag teams
 *
 * @author Étienne Muser
 */
public class TeamConfigManager {
    /**
     * The path to the team config file
     */
    private static final String teamConfigFilePath = LasertagMod.configFolderPath + "\\teamConfig.json";

    /**
     * The actual in-memory team config
     */
    public static HashMap<String, TeamDto> teamConfig = null;

    static {
        // TODO: team config leaks over into singleplayer from servers
        // TODO: Catch that teams must be unique
        // TODO: Make team config reloadable by command

        // Get config file
        var teamConfigFile = new File(teamConfigFilePath);

        // If the config file exists
        if (teamConfigFile.exists()) {
            try {
                // Read config file
                var configFileContents = FileIO.readAllFile(teamConfigFile);

                // get gson builder
                var gsonBuilder = TeamConfigManagerDeserializer.getDeserializer();

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
            var gsonBuilder = TeamDtoSerializer.getSerializer();

            // Serialize
            var configJson = gsonBuilder.setPrettyPrinting().create().toJson(teamConfig);

            // Persist
            try {
                var dir = new File(LasertagMod.configFolderPath);

                // Create directory if not exists
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        throw new IOException("Make directory for team config failed!");
                    }
                }

                // Create file if not exists
                if (!teamConfigFile.exists()) {
                    if (!teamConfigFile.createNewFile()) {
                        throw new IOException("Creation of file for team config failed!");
                    }
                }

                // Write to file
                FileIO.writeAllFile(teamConfigFile, configJson);
            } catch (IOException e) {
                LasertagMod.LOGGER.error("Writing to team config file failed: " + e.getMessage());
            }
        }
    }

    public static void syncTeamsToClient(ServerPlayerEntity player) {
        // Get gson builder
        var gsonBuilder = TeamDtoSerializer.getSerializer();

        // Serialize
        var configJson = gsonBuilder.create().toJson(teamConfig);

        // Create packet buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Write errorMessage to buffer
        buf.writeString(configJson);

        // Send to all clients
        ServerPlayNetworking.send(player, NetworkingConstants.LASERTAG_TEAMS_SYNC, buf);
    }
}

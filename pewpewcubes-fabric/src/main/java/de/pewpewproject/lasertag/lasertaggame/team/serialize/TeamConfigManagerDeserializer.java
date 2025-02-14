package de.pewpewproject.lasertag.lasertaggame.team.serialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.pewpewproject.lasertag.lasertaggame.team.TeamDto;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Util to get a deserializer for the TeamConfigManager class
 *
 * @author Étienne Muser
 */
public class TeamConfigManagerDeserializer {
    /**
     * Build a GsonBuilder for the TeamConfigManager
     * @return The GsonBuilder designed for the TeamConfigManager
     */
    public static JsonDeserializer<HashMap<String, TeamDto>> getDeserializer() {
        // Create deserializer for HashMap
        var deserializer = new JsonDeserializer<HashMap<String, TeamDto>>() {
            @Override
            public HashMap<String, TeamDto> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                // Create new hashmap
                var config = new HashMap<String, TeamDto>();

                // Get dictionary json object
                var jsonObject = jsonElement.getAsJsonObject();

                // For every key in the json object
                for (var teamName : jsonObject.keySet()) {
                    // Get the team of this key
                    var teamObject = jsonObject.get(teamName).getAsJsonObject();

                    // Get id
                    var id = teamObject.get("id").getAsInt();

                    // Get RGB
                    var r = teamObject.get("red").getAsInt();
                    var g = teamObject.get("green").getAsInt();
                    var b = teamObject.get("blue").getAsInt();

                    // Get id of spawnpoint block
                    var spawnpointBlockIdString = teamObject.get("spawnpointBlock").getAsString();
                    Block spawnPointBlock = null;

                    if (!spawnpointBlockIdString.equals("null")) {

                        var spawnpointBlockId = new Identifier(spawnpointBlockIdString);
                        spawnPointBlock = Registry.BLOCK.get(spawnpointBlockId);
                    }

                    // Create team
                    var teamDto = new TeamDto(id, teamName, r, g, b, spawnPointBlock);

                    config.put(teamName, teamDto);
                }

                return config;
            }
        };

        return deserializer;
    }
}

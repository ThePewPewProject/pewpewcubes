package de.kleiner3.lasertag.lasertaggame.teammanagement.serialize;

import com.google.gson.*;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamDto;
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
    public static GsonBuilder getDeserializer() {
        // Create builder
        var gsonBuilder = new GsonBuilder();

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

                    // Get RGB
                    var r = teamObject.get("red").getAsInt();
                    var g = teamObject.get("green").getAsInt();
                    var b = teamObject.get("blue").getAsInt();

                    // Get id of spawnpoint block
                    var spawnpointBlockId = new Identifier(teamObject.get("spawnpointBlock").getAsString());

                    // Create team
                    var teamDto = new TeamDto(teamName, r, g, b, Registry.BLOCK.get(spawnpointBlockId));

                    config.put(teamName, teamDto);
                }

                return config;
            }
        };

        // Register deserializer for HashMap
        gsonBuilder.registerTypeAdapter(HashMap.class, deserializer);

        return gsonBuilder;
    }
}

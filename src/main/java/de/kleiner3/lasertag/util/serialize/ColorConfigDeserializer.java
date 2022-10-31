package de.kleiner3.lasertag.util.serialize;

import com.google.gson.*;
import de.kleiner3.lasertag.types.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;
import java.util.HashMap;

public class ColorConfigDeserializer {
    public static GsonBuilder getDeserializer() {
        var gsonBuilder = new GsonBuilder();

        var deserializer = new JsonDeserializer<HashMap<String, Colors.Color>>() {
            @Override
            public HashMap<String, Colors.Color> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                // Create new hashmap
                var config = new HashMap<String, Colors.Color>();

                // Get dictionary json object
                var jsonObject = jsonElement.getAsJsonObject();

                // For every key in the json object
                for (var key : jsonObject.keySet()) {
                    // Get the team of this key
                    var teamObject = jsonObject.get(key).getAsJsonObject();

                    // The key is the team name
                    var teamName = key;

                    // Get RGB
                    var r = teamObject.get("red").getAsInt();
                    var g = teamObject.get("green").getAsInt();
                    var b = teamObject.get("blue").getAsInt();

                    // Get id of spawnpoint block
                    var spawnpointBlockId = new Identifier(teamObject.get("spawnpointBlock").getAsString());

                    // Create color
                    var color = new Colors.Color(teamName, r, g, b, Registry.BLOCK.get(spawnpointBlockId));

                    config.put(teamName, color);
                }

                return config;
            }
        };

        gsonBuilder.registerTypeAdapter(HashMap.class, deserializer);

        return gsonBuilder;
    }
}

package de.kleiner3.lasertag.util.serialize;

import com.google.gson.*;
import de.kleiner3.lasertag.types.Colors;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

/**
 * Util to get a serializer for the Color class
 *
 * @author Étienne Muser
 */
public class LasertagColorSerializer {
    /**
     * Build a GsonBuilder for the Color
     * @return The GsonBuilder designed for the Color
     */
    public static GsonBuilder getSerializer() {
        // Create builder
        var gsonBuilder = new GsonBuilder();

        // Create serializer for Color
        var serializer = new JsonSerializer<Colors.Color>() {
            @Override
            public JsonElement serialize(Colors.Color color, Type type, JsonSerializationContext jsonSerializationContext) {
                var jsonObject = new JsonObject();

                // Add RGB
                jsonObject.addProperty("red", color.getR());
                jsonObject.addProperty("green", color.getG());
                jsonObject.addProperty("blue", color.getB());

                // Add spawnpoint block
                jsonObject.addProperty("spawnpointBlock", Registry.BLOCK.getId(color.getSpawnpointBlock()).toString());

                return jsonObject;
            }
        };

        // Register serializer for Color
        gsonBuilder.registerTypeAdapter(Colors.Color.class, serializer);

        return gsonBuilder;
    }
}

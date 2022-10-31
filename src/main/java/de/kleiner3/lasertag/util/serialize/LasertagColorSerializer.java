package de.kleiner3.lasertag.util.serialize;

import com.google.gson.*;
import de.kleiner3.lasertag.types.Colors;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

public class LasertagColorSerializer {
    public static GsonBuilder getSerializer() {
        var gsonBuilder = new GsonBuilder();

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

        gsonBuilder.registerTypeAdapter(Colors.Color.class, serializer);

        return gsonBuilder;
    }
}

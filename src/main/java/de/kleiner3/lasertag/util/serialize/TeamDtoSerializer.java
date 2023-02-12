package de.kleiner3.lasertag.util.serialize;

import com.google.gson.*;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamDto;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Type;

/**
 * Util to get a serializer for the TeamDto class
 *
 * @author Étienne Muser
 */
public class TeamDtoSerializer {
    /**
     * Build a GsonBuilder for the Team
     * @return The GsonBuilder designed for the Team
     */
    public static GsonBuilder getSerializer() {
        // Create builder
        var gsonBuilder = new GsonBuilder();

        // Create serializer for TeamDto
        var serializer = new JsonSerializer<TeamDto>() {
            @Override
            public JsonElement serialize(TeamDto teamDto, Type type, JsonSerializationContext jsonSerializationContext) {
                var jsonObject = new JsonObject();

                // Add RGB
                jsonObject.addProperty("red", teamDto.color().r());
                jsonObject.addProperty("green", teamDto.color().g());
                jsonObject.addProperty("blue", teamDto.color().b());

                // Add spawnpoint block
                jsonObject.addProperty("spawnpointBlock", Registry.BLOCK.getId(teamDto.spawnpointBlock()).toString());

                return jsonObject;
            }
        };

        // Register serializer for TeamDto
        gsonBuilder.registerTypeAdapter(TeamDto.class, serializer);

        return gsonBuilder;
    }
}

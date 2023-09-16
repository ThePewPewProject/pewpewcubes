package de.kleiner3.lasertag.lasertaggame.management.team.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
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
    public static JsonSerializer<TeamDto> getSerializer() {
        // Create serializer for TeamDto
        var serializer = new JsonSerializer<TeamDto>() {
            @Override
            public JsonElement serialize(TeamDto teamDto, Type type, JsonSerializationContext jsonSerializationContext) {
                var jsonObject = new JsonObject();

                // Add id
                jsonObject.addProperty("id", teamDto.id());

                // Add RGB
                jsonObject.addProperty("red", teamDto.color().r());
                jsonObject.addProperty("green", teamDto.color().g());
                jsonObject.addProperty("blue", teamDto.color().b());

                // Add spawnpoint block
                var spawnPointBlockId = "null";
                if (teamDto.spawnpointBlock() != null) {
                    
                    spawnPointBlockId = Registry.BLOCK.getId(teamDto.spawnpointBlock()).toString();
                }
                jsonObject.addProperty("spawnpointBlock", spawnPointBlockId);

                return jsonObject;
            }
        };

        return serializer;
    }
}

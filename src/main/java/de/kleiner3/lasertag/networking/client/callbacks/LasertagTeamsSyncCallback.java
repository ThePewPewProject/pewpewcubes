package de.kleiner3.lasertag.networking.client.callbacks;

import com.google.gson.reflect.TypeToken;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamConfigManager;
import de.kleiner3.lasertag.lasertaggame.teammanagement.TeamDto;
import de.kleiner3.lasertag.util.serialize.TeamConfigManagerDeserializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;

/**
 * Callback to handle the lasertag teams sync network event
 *
 * @author Étienne Muser
 */
public class LasertagTeamsSyncCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // Get json string
        var jsonString = buf.readString();

        // get gson builder
        var gsonBuilder = TeamConfigManagerDeserializer.getDeserializer();

        // Parse
        TeamConfigManager.teamConfig = gsonBuilder
                .create()
                .fromJson(jsonString,
                        new TypeToken<HashMap<String, TeamDto>>() {}.getType());
    }
}

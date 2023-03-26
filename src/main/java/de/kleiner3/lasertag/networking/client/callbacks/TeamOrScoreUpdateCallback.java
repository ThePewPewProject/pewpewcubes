package de.kleiner3.lasertag.networking.client.callbacks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Callback to handle the team or score update network event
 *
 * @author Étienne Muser
 */
public class TeamOrScoreUpdateCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        LasertagGameManager.getInstance().getHudRenderManager().teamMap = new Gson().fromJson(buf.readString(),
                new TypeToken<HashMap<String, LinkedList<Tuple<String, Long>>>>() {
                }.getType());
    }
}

package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.client.screen.LasertagLoadingScreen;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback to handle the map loading network event
 *
 * @author Étienne Muser
 */
public class MapLoadingEventCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        var stepString = buf.readString();
        var newProgress = buf.readDouble();

        var renderData = LasertagGameManager.getInstance().getHudRenderManager();

        var oldProgress = renderData.mapLoadingProgress;
        renderData.mapLoadingStepString = stepString;
        renderData.mapLoadingProgress = newProgress;

        if (oldProgress == -1 && newProgress >= 0) {
            client.execute(() -> client.setScreen(new LasertagLoadingScreen()));
            return;
        }

        if (oldProgress >= 0 && newProgress == -1) {
            client.execute(() -> client.setScreen(null));
        }
    }
}

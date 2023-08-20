package de.kleiner3.lasertag.networking.server;

import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.callbacks.PlayerHitLasertargetCallback;
import de.kleiner3.lasertag.networking.server.callbacks.PlayerHitPlayerCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Class to handle all networking on the server
 *
 * @author Étienne Muser
 */
public class ServerNetworkingHandler {
    public void register() {
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_HIT_LASERTARGET, new PlayerHitLasertargetCallback());
        ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.PLAYER_HIT_PLAYER, new PlayerHitPlayerCallback());
    }
}

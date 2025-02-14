package de.pewpewproject.lasertag.networking;

import de.pewpewproject.lasertag.networking.server.ServerNetworkingHandler;

/**
 * Class for registering all networking handlers
 *
 * @author Étienne Muser
 */
public class ServerNetworkingHandlers {
    public static final ServerNetworkingHandler SERVER_NETWORKING_HANDLER = new ServerNetworkingHandler();

    public static void register() {
        SERVER_NETWORKING_HANDLER.register();
    }
}

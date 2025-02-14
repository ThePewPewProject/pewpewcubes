package de.pewpewproject.lasertag.networking;

import de.pewpewproject.lasertag.networking.client.ClientNetworkingHandler;

/**
 * Class for registering all networking handlers
 *
 * @author Étienne Muser
 */
public class ClientNetworkingHandlers {
    public static final ClientNetworkingHandler CLIENT_NETWORKING_HANDLER = new ClientNetworkingHandler();

    public static void register() {
        CLIENT_NETWORKING_HANDLER.register();
    }
}

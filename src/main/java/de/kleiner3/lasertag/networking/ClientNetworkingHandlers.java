package de.kleiner3.lasertag.networking;

import de.kleiner3.lasertag.networking.client.ClientNetworkingHandler;

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

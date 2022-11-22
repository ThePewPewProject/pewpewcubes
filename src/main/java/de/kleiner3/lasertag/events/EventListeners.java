package de.kleiner3.lasertag.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * Class for registering all event listeners
 *
 * @author Étienne Muser
 */
public class EventListeners {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(PlayerJoinEventHandler::onPlayerJoin);
        // TODO: Reset HUD on disconnect/leave world
    }
}

package de.kleiner3.lasertag.events;

import de.kleiner3.lasertag.events.callback.PlayerJoinEventHandler;
import de.kleiner3.lasertag.events.callback.ServerWorldUnloadedEventHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * Class for registering all event listeners
 *
 * @author Étienne Muser
 */
public class EventListeners {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(PlayerJoinEventHandler::onPlayerJoin);

        ServerWorldEvents.UNLOAD.register(ServerWorldUnloadedEventHandler::onServerWorldUnloaded);
    }
}

package de.kleiner3.lasertag.command;

import de.kleiner3.lasertag.command.lasertag.CreditsCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ClientCommandInitializer {
    public static void initCommands() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            CreditsCommand.register(dispatcher);
        }));
    }
}

package de.kleiner3.lasertag.command;

import de.kleiner3.lasertag.command.lasertag.CreditsCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

/**
 * Initializes the client side commands
 *
 * @author Étienne Muser
 */
public class ClientCommandInitializer {
    /**
     * Init client side commands
     */
    public static void initCommands() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            CreditsCommand.register(dispatcher);
        }));
    }
}

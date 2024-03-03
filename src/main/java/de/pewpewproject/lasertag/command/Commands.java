package de.pewpewproject.lasertag.command;

import de.pewpewproject.lasertag.command.lasertag.game.LasertagCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

/**
 * Initializes the server side commands
 *
 * @author Étienne Muser
 */
public class Commands {
    /**
     * Init server side commands
     */
    public static void register() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> LasertagCommand.register(dispatcher)));
    }
}

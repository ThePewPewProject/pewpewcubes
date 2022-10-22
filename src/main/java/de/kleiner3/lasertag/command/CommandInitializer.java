package de.kleiner3.lasertag.command;

import de.kleiner3.lasertag.command.lasertag.game.LasertagCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandInitializer {
    public static void initCommands() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            LasertagCommand.register(dispatcher);
        }));
    }
}

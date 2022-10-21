package de.kleiner3.lasertag.command;

import de.kleiner3.lasertag.command.lasertag.game.LasertagRulesCommand;
import de.kleiner3.lasertag.command.lasertag.game.StartGameCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandInitializer {
    public static void initCommands() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            StartGameCommand.register(dispatcher);
            LasertagRulesCommand.register(dispatcher);
        }));
    }
}

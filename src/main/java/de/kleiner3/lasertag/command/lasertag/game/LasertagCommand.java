package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.lasertag.game.settings.LasertagSettingsCommand;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag command
 *
 * @author Étienne Muser
 */
public class LasertagCommand {
    // TODO: Dont allow starting a game or switching/leaving teams while a game is running
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> ignoredContext) {
        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cmd = literal("lasertag")
                .executes(LasertagCommand::execute);

        StartLasertagGameCommand.register(cmd);
        JoinLasertagTeamCommand.register(cmd);
        LeaveLasertagTeamCommand.register(cmd);
        LasertagSettingsCommand.register(cmd);

        dispatcher.register(cmd);
    }
}

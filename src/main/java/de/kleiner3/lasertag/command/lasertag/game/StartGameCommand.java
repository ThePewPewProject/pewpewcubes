package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class StartGameCommand {
    private static int execute(CommandContext<ServerCommandSource> context) {
        context.getSource().getServer().startGame();
        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher dispatcher) {
        dispatcher.register(literal("startLasertagGame")
                .executes(ctx -> execute(ctx)));
    }
}

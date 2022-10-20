package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.core.jmx.Server;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The command to start the lasertag game
 *
 * @author Étienne Muser
 */
public class StartGameCommand {
    /**
     * Execute the start lasertag command
     * @param context
     * @param scanSpawnpoints
     * @return
     */
    private static int execute(CommandContext<ServerCommandSource> context, boolean scanSpawnpoints) {
        context.getSource().getServer().startGame(scanSpawnpoints);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Execute the start lasertag command without searching for spawnpoint blocks
     * @param context
     * @return
     */
    private static int execute(CommandContext<ServerCommandSource> context) {
        return execute(context, false);
    }

    /**
     * Register the start lasertag game command
     * @param dispatcher
     */
    public static void register(CommandDispatcher dispatcher) {
        dispatcher.register(literal("startLasertagGame")
                .executes(ctx -> execute(ctx))
                .then(literal("scanSpawnpoints")
                        .executes(ctx -> execute(ctx, true))));
    }
}

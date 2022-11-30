package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The start lasertag game command
 *
 * @author Étienne Muser
 */
public class StartLasertagGameCommand {
    /**
     * Execute the start lasertag command
     *
     * @param context The CommandContext
     * @param scanSpawnpoints Bool if the world should be scanned for the spawnpoint blocks again
     * @return Return code
     */
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context, boolean scanSpawnpoints) {
        context.getSource().getServer().startGame(scanSpawnpoints);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Execute the start lasertag command without searching for spawnpoint blocks
     *
     * @param context The CommandContext
     * @return Return code
     */
    private static int execute(CommandContext<ServerCommandSource> context) {
        return execute(context, false);
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("startLasertagGame")
                .requires(s -> s.hasPermissionLevel(1))
                .executes(StartLasertagGameCommand::execute)
                .then(literal("scanSpawnpoints")
                        .executes(ctx -> execute(ctx, true))));
    }
}

package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.LasertagConfig;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class LasertagCommand {
    private static int execute(CommandContext<ServerCommandSource> context) {
        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher dispatcher) {
        var cmd = literal("lasertag")
                .executes(ctx -> execute(ctx));

        RenderHudSetting.register(cmd);
        StartGame.register(cmd);

        dispatcher.register(cmd);
    }

    private class RenderHudSetting {
        private static int execute(CommandContext<ServerCommandSource> context) {
            LasertagConfig.renderTeamList = BoolArgumentType.getBool(context, "value");
            return Command.SINGLE_SUCCESS;
        }

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            lab.then(literal("renderTeamList")
                    .then(argument("value", bool())
                            .executes(ctx -> execute(ctx))));
        }
    }

    private class StartGame {
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

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            lab.then(literal("startLasertagGame")
                    .executes(ctx -> execute(ctx))
                    .then(literal("scanSpawnpoints")
                            .executes(ctx -> execute(ctx, true))));
        }
    }

    private class JoinTeam {}

    private class LeaveTeam {}
}

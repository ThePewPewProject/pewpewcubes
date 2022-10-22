package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.command.suggestions.TeamSuggestionProvider;
import de.kleiner3.lasertag.types.Colors;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
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
        JoinTeam.register(cmd);

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

    private class JoinTeam {
        private static int execute(CommandContext<ServerCommandSource> context) {
            // Get the team
            var teamName = StringArgumentType.getString(context, "team");

            // Get the server
            var server = context.getSource().getServer();

            // Get executing player
            var player = context.getSource().getPlayer();

            // Get team color
            var teamColor = Colors.colorConfig.get(teamName);

            // Join team
            server.playerJoinTeam(teamColor, player);

            return Command.SINGLE_SUCCESS;
        }

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            lab.then(literal("joinTeam")
                    .then(argument("team", word())
                            .suggests(TeamSuggestionProvider.getInstance())
                            .executes(ctx -> execute(ctx))));
        }
    }

    private class LeaveTeam {}
}

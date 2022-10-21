package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.LasertagConfig;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class LasertagRulesCommand {
    private static int execute(CommandContext<ServerCommandSource> context) {
        return 0;
    }

    public static void register(CommandDispatcher dispatcher) {
        var cmd = literal("lasertagSettings")
                .executes(ctx -> execute(ctx));

        RenderHudSetting.register(cmd);

        dispatcher.register(cmd);
    }

    private class RenderHudSetting {
        private static int execute(CommandContext<ServerCommandSource> context) {
            LasertagConfig.renderTeamList = BoolArgumentType.getBool(context, "value");
            return 0;
        }

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            lab.then(literal("renderTeamList")
                    .then(argument("value", bool())
                            .executes(ctx -> execute(ctx))));
        }
    }
}

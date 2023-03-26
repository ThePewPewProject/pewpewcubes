package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingNames;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag player deactivation duration setting
 *
 * @author Étiennne Muser
 */
public class PlayerDeactivationDurationSettingCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        var value = LongArgumentType.getLong(context, "duration");
        LasertagGameManager.getInstance().getSettingsManager().set(context.getSource().getServer(), SettingNames.DEACTIVATE_TIME, value);
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting playerDeactivationDuration is now set to " + value), false);
        return Command.SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("playerDeactivateDuration")
                .then(argument("duration", longArg(0))
                        .executes(PlayerDeactivationDurationSettingCommand::execute)));
    }
}

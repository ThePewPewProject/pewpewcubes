package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.lasertaggame.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.lasertaggame.settings.SettingNames;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag game duration setting
 *
 * @author Étienne Muser
 */
public class LasertagGameDurationSettingCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        var value = IntegerArgumentType.getInteger(context, "duration");
        LasertagSettingsManager.set(context.getSource().getServer(), SettingNames.PLAY_TIME, value);
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting gameDuration is now set to " + value), false);
        return Command.SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("gameDuration")
                .then(argument("duration", integer(1))
                        .executes(LasertagGameDurationSettingCommand::execute)));
    }
}

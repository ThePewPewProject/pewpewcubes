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
 * The lasertag player deactivation duration setting
 *
 * @author Étiennne Muser
 */
public class PlayerDeactivationDurationSettingCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        var value = IntegerArgumentType.getInteger(context, "duration");
        LasertagSettingsManager.set(context.getSource().getServer(), SettingNames.DEACTIVATE_TIME, value);
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting playerDeactivationDuration is now set to " + value), false);
        return Command.SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("playerDeactivateDuration")
                .then(argument("duration", integer(0))
                        .executes(PlayerDeactivationDurationSettingCommand::execute)));
    }
}

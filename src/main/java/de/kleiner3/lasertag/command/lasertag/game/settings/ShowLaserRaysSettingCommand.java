package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingNames;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The show laser ray setting
 *
 * @author Étienne Muser
 */
public class ShowLaserRaysSettingCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        var value = BoolArgumentType.getBool(context, "value");
        LasertagGameManager.getInstance().getSettingsManager().set(context.getSource().getServer(), SettingNames.SHOW_LASER_RAYS, value);
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting showLaserRays is now set to " + value), false);
        return Command.SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("showLaserRays")
                .then(argument("value", bool())
                        .executes(ShowLaserRaysSettingCommand::execute)));
    }
}

package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.LasertagConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag weapon reach distance command
 *
 * @author Étienne Muser
 */
public class LasertagWeaponReachSettingCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        var value = IntegerArgumentType.getInteger(context, "distance");
        LasertagConfig.getInstance().setLasertagWeaponReach(context.getSource().getServer(), value);
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting lasertagWeaponReach is now set to " + value), false);
        return Command.SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("lasertagWeaponReach")
                .then(argument("distance", integer(0))
                        .executes(LasertagWeaponReachSettingCommand::execute)));
    }
}

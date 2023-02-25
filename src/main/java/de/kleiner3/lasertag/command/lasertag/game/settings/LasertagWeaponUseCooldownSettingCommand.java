package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.lasertaggame.settings.LasertagSettingsManager;
import de.kleiner3.lasertag.lasertaggame.settings.SettingNames;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag weapon use cooldown duration command
 *
 * @author Étienne Muser
 */
public class LasertagWeaponUseCooldownSettingCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        var value = LongArgumentType.getLong(context, "ticks");
        LasertagSettingsManager.set(context.getSource().getServer(), SettingNames.WEAPON_COOLDOWN, value);
        context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting lasertagWeaponCooldown is now set to " + value), false);
        return Command.SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("lasertagWeaponUseCooldown")
                .then(argument("ticks", longArg(0))
                        .executes(LasertagWeaponUseCooldownSettingCommand::execute)));
    }
}

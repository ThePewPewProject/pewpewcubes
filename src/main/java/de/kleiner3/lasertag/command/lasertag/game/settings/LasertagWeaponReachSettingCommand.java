package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingNames;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag weapon reach distance command
 *
 * @author Étienne Muser
 */
public class LasertagWeaponReachSettingCommand extends ServerFeedbackCommand {
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {
        var value = LongArgumentType.getLong(context, "distance");
        LasertagGameManager.getInstance().getSettingsManager().set(context.getSource().getServer(), SettingNames.WEAPON_REACH, value);

        return Optional.of(new CommandFeedback(Text.literal("Lasertag setting lasertagWeaponReach is now set to " + value), false, true));
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("lasertagWeaponReach")
                .then(argument("distance", longArg(0))
                        .executes(new LasertagWeaponReachSettingCommand())));
    }
}

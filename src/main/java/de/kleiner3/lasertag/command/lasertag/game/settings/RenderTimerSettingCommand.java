package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingNames;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The render timer setting
 *
 * @author Étienne Muser
 */
public class RenderTimerSettingCommand extends ServerFeedbackCommand {
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {
        var value = BoolArgumentType.getBool(context, "value");
        LasertagGameManager.getInstance().getSettingsManager().set(context.getSource().getServer(), SettingNames.RENDER_TIMER, value);

        return Optional.of(new CommandFeedback(Text.literal("Lasertag setting renderTimer is now set to " + value), false, true));
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("renderTimer")
                .then(argument("value", bool())
                        .executes(new RenderTimerSettingCommand())));
    }
}

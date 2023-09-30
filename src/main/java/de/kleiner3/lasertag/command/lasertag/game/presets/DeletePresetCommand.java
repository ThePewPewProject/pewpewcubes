package de.kleiner3.lasertag.command.lasertag.game.presets;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The delete preset command
 *
 * @author Étienne Muser
 */
public class DeletePresetCommand extends ServerFeedbackCommand {
    @Override
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        var presetName = getString(context, "name");

        var server = context.getSource().getServer();
        var successful = server.getLasertagServerManager().getSettingsPresetsManager().deletePreset(presetName);
        LasertagGameManager.getInstance().getPresetsNameManager().removePresetName(server, presetName);

        if (successful) {
            return Optional.of(new CommandFeedback(Text.literal("Deleted settings preset '" + presetName + "'"), true, false));
        } else {
            return Optional.of(new CommandFeedback(Text.literal("Settings preset '" + presetName + "' does not exist").formatted(Formatting.RED), true, false));
        }
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("delete")
                .then(argument("name", string())
                        .executes(new DeletePresetCommand())));
    }
}
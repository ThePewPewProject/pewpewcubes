package de.kleiner3.lasertag.command.lasertag.game.presets;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The save preset command
 *
 * @author Étienne Muser
 */
public class SavePresetCommand extends ServerFeedbackCommand {
    @Override
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        var presetName = getString(context, "name");

        var server = context.getSource().getServer();
        server.getLasertagServerManager().getSettingsPresetsManager().savePreset(presetName);
        LasertagGameManager.getInstance().getPresetsNameManager().addPresetName(server, presetName);

        return Optional.of(new CommandFeedback(Text.literal("Saved settings preset '" + presetName + "'"), true, false));
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("save")
                .then(argument("name", string())
                        .executes(new SavePresetCommand())));
    }
}

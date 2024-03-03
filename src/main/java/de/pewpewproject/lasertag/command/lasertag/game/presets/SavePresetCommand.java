package de.pewpewproject.lasertag.command.lasertag.game.presets;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
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

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var settingsPresetsManager = gameManager.getSettingsPresetsManager();
        var settingsPresetsNameManager = gameManager.getSettingsPresetsNameManager();

        var presetName = getString(context, "name");

        settingsPresetsManager.savePreset(presetName);
        settingsPresetsNameManager.addPresetName(presetName);

        return Optional.of(new CommandFeedback(Text.literal("Saved settings preset '" + presetName + "'"), true, false));
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("save")
                .then(argument("name", string())
                        .executes(new SavePresetCommand())));
    }
}

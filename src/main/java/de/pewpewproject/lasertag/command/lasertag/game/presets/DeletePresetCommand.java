package de.pewpewproject.lasertag.command.lasertag.game.presets;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var settingsPresetsManager = gameManager.getSettingsPresetsManager();
        var settingsPresetsNameManager = gameManager.getSettingsPresetsNameManager();

        var presetName = getString(context, "name");

        var successful = settingsPresetsManager.deletePreset(presetName);
        settingsPresetsNameManager.removePresetName(presetName);

        if (successful) {
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Deleted settings preset '" + presetName + "'"), true, false)));
        } else {
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Settings preset '" + presetName + "' does not exist").formatted(Formatting.RED), true, false)));
        }
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("delete")
                .then(argument("name", string())
                        .executes(new DeletePresetCommand())));
    }
}

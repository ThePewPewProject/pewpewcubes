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
 * The load preset command
 *
 * @author Étienne Muser
 */
public class LoadPresetCommand extends ServerFeedbackCommand {
    @Override
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var settingsPresetsManager = gameManager.getSettingsPresetsManager();

        // If a game is running
        if (gameManager.isGameRunning()) {
            // Cannot change settings in-game
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Cannot change settings while a game is running").formatted(Formatting.RED), true, false)));
        }

        var presetName = getString(context, "name");

        var successful = settingsPresetsManager.loadPreset(presetName);

        if (successful) {
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Loaded settings preset '" + presetName + "'"), true, true)));
        } else {
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Settings preset '" + presetName + "' does not exist").formatted(Formatting.RED), true, false)));
        }
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("load")
                .then(argument("name", string())
                        .executes(new LoadPresetCommand())));
    }
}

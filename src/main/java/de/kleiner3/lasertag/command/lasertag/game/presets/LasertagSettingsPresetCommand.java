package de.kleiner3.lasertag.command.lasertag.game.presets;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag settings preset command
 *
 * @author Étienne Muser
 */
public class LasertagSettingsPresetCommand extends ServerFeedbackCommand {
    @Override
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var settingsPresetsNameManager = gameManager.getSettingsPresetsNameManager();

        var builder = new StringBuilder();
        var presetNames = settingsPresetsNameManager.getSettingsPresetNames();

        for (var presetName : presetNames) {
            builder.append(presetName);
            builder.append(", ");
        }

        return Optional.of(new CommandFeedback(Text.literal(builder.toString()), false, false));
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        var cmd = literal("settingsPresets")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(new LasertagSettingsPresetCommand());

        SavePresetCommand.register(cmd);
        LoadPresetCommand.register(cmd);
        DeletePresetCommand.register(cmd);

        lab.then(cmd);
    }
}

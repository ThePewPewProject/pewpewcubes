package de.pewpewproject.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag settings command
 *
 * @author Étienne Muser
 */
public class LasertagSettingsCommand extends ServerFeedbackCommand {
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();

        return Optional.of(new CommandFeedback(Text.literal(settingsManager.toString()), false, false));
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        var cmd = literal("settings")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(new LasertagSettingsCommand());

        SettingsCommandBuilder.buildSettingsCommands(cmd);

        lab.then(cmd);
    }
}

package de.pewpewproject.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The reset lasertag settings command
 *
 * @author Étienne Muser
 */
public class ResetSettingsCommand extends ServerFeedbackCommand {
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();

        settingsManager.reset();

        return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Settings are reset."), false, true)));
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("settingsReset")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(new ResetSettingsCommand()));
    }
}

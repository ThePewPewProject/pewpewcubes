package de.pewpewproject.lasertag.command.lasertag.game;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The stop lasertag game command
 *
 * @author Étienne Muser
 */
public class StopLasertagGameCommand extends ServerFeedbackCommand {
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();

        // If there is no game
        if (!gameManager.isGameRunning()) {
            // No game running
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("There is currently no game running").formatted(Formatting.RED), true, false)));
        }

        gameManager.stopLasertagGame();

        return CompletableFuture.completedFuture(Optional.empty());
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("stopLasertagGame")
                .requires(s -> s.hasPermissionLevel(1))
                .executes(new StopLasertagGameCommand()));
    }
}

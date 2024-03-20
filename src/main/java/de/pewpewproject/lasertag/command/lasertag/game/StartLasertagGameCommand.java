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
 * The start lasertag game command
 *
 * @author Étienne Muser
 */
public class StartLasertagGameCommand extends ServerFeedbackCommand {
    private boolean scanSpawnpoints;

    private StartLasertagGameCommand(boolean scanSpawnpoints) {
        this.scanSpawnpoints = scanSpawnpoints;
    }

    @Override
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();

        // If a game is running
        if (gameManager.isGameRunning()) {
            // Game is already running
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("There is already a game running").formatted(Formatting.RED), true, false)));
        }

        return gameManager.startGame(scanSpawnpoints)
                .thenApplyAsync(abortReasons -> abortReasons
                        .map(s -> new CommandFeedback(Text.literal("Start game aborted. Reasons:\n" + s)
                                .formatted(Formatting.RED), false, true)));
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("startLasertagGame")
                .requires(s -> s.hasPermissionLevel(1))
                .executes(new StartLasertagGameCommand(false))
                .then(literal("scanSpawnpoints")
                        .executes(new StartLasertagGameCommand(true))));
    }
}

package de.pewpewproject.lasertag.command.lasertag.game;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import de.pewpewproject.lasertag.command.suggestions.LasertagGameModeSuggestionProvider;
import de.pewpewproject.lasertag.lasertaggame.gamemode.GameModes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag game mode command
 *
 * @author Étienne Muser
 */
public class LasertagGameModeCommand extends ServerFeedbackCommand {

    @Override
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var gameModeManager = gameManager.getGameModeManager();

        // If a game is running
        if (gameManager.isGameRunning()) {
            // Cannot change game mode in-game
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Cannot change game mode while a game is running").formatted(Formatting.RED), true, false)));
        }

        // Get the game mode translatable name
        var gameModeTranslatableName = StringArgumentType.getString(context, "gamemode");

        // Get the new game mode
        var newGameMode = GameModes.GAME_MODES.get(gameModeTranslatableName);

        // Sanity check
        if (newGameMode == null) {
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Invalid game mode.").formatted(Formatting.RED), false, false)));
        }

        // Set the game mode
        gameModeManager.setGameMode(newGameMode);

        // Translate the game mode name
        var translatedGameModeName = Text.translatable(gameModeTranslatableName).getString();

        return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Game mode changed to '" + translatedGameModeName + "'."), false, true)));
    }

    private static int executeWithoutArgs(CommandContext<ServerCommandSource> context) {

        // Get the source
        var source = context.getSource();

        // Get the player
        var player = source.getPlayer();

        // Get the game managers
        var gameManager = source.getWorld().getServerLasertagManager();
        var gameModeManager = gameManager.getGameModeManager();

        // Get the game mode name
        var gameModeName = Text.translatable(gameModeManager.getGameMode().getTranslatableName());

        // Build the message string
        var message = Text.translatable("chat.message.game_mode_print", gameModeName);

        // Send chat message to the player
        player.sendMessage(message, false);

        return SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("gamemode")
                        .executes(LasertagGameModeCommand::executeWithoutArgs)
                        .then(argument("gamemode", word())
                                .requires(s -> s.hasPermissionLevel(1))
                                .suggests(LasertagGameModeSuggestionProvider.getInstance())
                                .executes(new LasertagGameModeCommand())));
    }
}

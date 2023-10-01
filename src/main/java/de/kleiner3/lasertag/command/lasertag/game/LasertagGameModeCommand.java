package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import de.kleiner3.lasertag.command.suggestions.LasertagGameModeSuggestionProvider;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.GameModes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

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
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        // Get the game mode translatable name
        var gameModeTranslatableName = StringArgumentType.getString(context, "gamemode");

        // Get the server
        var server = context.getSource().getServer();

        // Get the new game mode
        var newGameMode = GameModes.GAME_MODES.get(gameModeTranslatableName);

        // Sanity check
        if (newGameMode == null) {
            return Optional.of(new CommandFeedback(Text.literal("Invalid game mode.").formatted(Formatting.RED), false, false));
        }

        // Set the game mode
        LasertagGameManager.getInstance().getGameModeManager().setGameMode(server, newGameMode);

        // Translate the game mode name
        var translatedGameModeName = Text.translatable(gameModeTranslatableName).getString();

        return Optional.of(new CommandFeedback(Text.literal("Game mode changed to '" + translatedGameModeName + "'."), false, true));
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("gamemode")
                        .then(argument("gamemode", word())
                                .suggests(LasertagGameModeSuggestionProvider.getInstance())
                                .executes(new LasertagGameModeCommand())));
    }
}

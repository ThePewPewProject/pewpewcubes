package de.pewpewproject.lasertag.command.suggestions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.pewpewproject.lasertag.lasertaggame.gamemode.GameModes;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

/**
 * Suggestion provider for game modes
 *
 * @author Étienne Muser
 */
public class LasertagGameModeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    private static LasertagGameModeSuggestionProvider instance = null;

    private LasertagGameModeSuggestionProvider() {
    }

    public static LasertagGameModeSuggestionProvider getInstance() {
        if (instance == null) {
            instance = new LasertagGameModeSuggestionProvider();
        }

        return instance;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        boolean inputEmpty = false;
        // Try to get current input
        String input = null;
        try {
            input = StringArgumentType.getString(context, "gamemode");
        } catch (IllegalArgumentException ex) {
            inputEmpty = true;
        }

        for (var gameModeTranslatableName : GameModes.GAME_MODES.keySet()) {

            if (inputEmpty || gameModeTranslatableName.toLowerCase().contains(input.toLowerCase())) {
                builder.suggest(gameModeTranslatableName);
            }
        }

        return builder.buildFuture();
    }
}

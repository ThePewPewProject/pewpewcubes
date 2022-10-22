package de.kleiner3.lasertag.command.suggestions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.kleiner3.lasertag.types.Colors;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class TeamSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    private static TeamSuggestionProvider instance = null;

    private TeamSuggestionProvider() {
    }

    public static TeamSuggestionProvider getInstance() {
        if (instance == null) {
            instance = new TeamSuggestionProvider();
        }

        return instance;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        boolean inputEmpty = false;
        // Try to get current input
        String input = null;
        try {
            input = StringArgumentType.getString(context, "team");
        } catch (IllegalArgumentException ex) {
            inputEmpty = true;
        }

        for (String color : Colors.colorConfig.keySet()) {
            if (inputEmpty || color.toLowerCase().startsWith(input.toLowerCase())) {
                builder.suggest(color);
            }
        }

        return builder.buildFuture();
    }
}

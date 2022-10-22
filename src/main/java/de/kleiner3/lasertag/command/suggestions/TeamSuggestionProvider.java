package de.kleiner3.lasertag.command.suggestions;

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


        for (String color : Colors.colorConfig.keySet()) {
            builder.suggest(color);
        }

        return builder.buildFuture();
    }
}

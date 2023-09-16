package de.kleiner3.lasertag.command.suggestions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaType;
import de.kleiner3.lasertag.worldgen.chunkgen.ProceduralArenaType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

/**
 * Suggestion provider for the arenas
 *
 * @author Étienne Muser
 */
public class MapSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    private static MapSuggestionProvider instance = null;

    private MapSuggestionProvider() {}

    public static MapSuggestionProvider getInstance() {
        if (instance == null) {
            instance = new MapSuggestionProvider();
        }

        return instance;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        boolean inputEmpty = false;
        // Try to get current input
        String input = null;
        try {
            input = StringArgumentType.getString(context, "map");
        } catch (IllegalArgumentException ex) {
            inputEmpty = true;
        }

        for (var map : ArenaType.values()) {
            if (map == ArenaType.PROCEDURAL) {
                for (var proceduralMap : ProceduralArenaType.values()) {
                    if (inputEmpty || proceduralMap.translatableName.toLowerCase().contains(input.toLowerCase())) {
                        builder.suggest(proceduralMap.translatableName);
                    }
                }
            } else {
                if (inputEmpty || map.translatableName.toLowerCase().contains(input.toLowerCase())) {
                    builder.suggest(map.translatableName);
                }
            }
        }

        return builder.buildFuture();
    }
}

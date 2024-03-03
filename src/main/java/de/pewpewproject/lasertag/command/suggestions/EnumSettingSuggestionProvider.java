package de.pewpewproject.lasertag.command.suggestions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

/**
 * Suggestion provider for enum settings
 *
 * @author Étienne Muser
 */
public class EnumSettingSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    private final Class<?> enumType;

    public EnumSettingSuggestionProvider(Class<?> enumType) {
        this.enumType = enumType;
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

        // Get the enum values
        var enumValues = this.enumType.getEnumConstants();

        for (var enumValue : enumValues) {
            var enumName = ((Enum<?>)enumValue).name();

            if (inputEmpty || enumName.toLowerCase().startsWith(input.toLowerCase())) {
                builder.suggest(enumName);
            }
        }

        return builder.buildFuture();
    }
}

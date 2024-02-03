package de.kleiner3.lasertag.command.suggestions;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

/**
 * Suggestion provider for the teams
 *
 * @author Étienne Muser
 */
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
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var syncedState = gameManager.getSyncedState();
        var teamsConfigState = syncedState.getTeamsConfigState();

        boolean inputEmpty = false;
        // Try to get current input
        String input = null;
        try {
            input = StringArgumentType.getString(context, "team");
        } catch (IllegalArgumentException ex) {
            inputEmpty = true;
        }

        for (var team :  teamsConfigState.getTeams()) {
            if (inputEmpty || team.name().toLowerCase().startsWith(input.toLowerCase())) {
                builder.suggest(team.name());
            }
        }

        return builder.buildFuture();
    }
}

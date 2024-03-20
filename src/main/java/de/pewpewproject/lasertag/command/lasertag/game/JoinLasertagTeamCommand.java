package de.pewpewproject.lasertag.command.lasertag.game;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import de.pewpewproject.lasertag.command.suggestions.TeamSuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The join lasertag team command
 *
 * @author Étiennne Muser
 */
public class JoinLasertagTeamCommand extends ServerFeedbackCommand {
    @Override
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var syncedState = gameManager.getSyncedState();
        var teamsConfigState = syncedState.getTeamsConfigState();

        // If a game is running
        if (gameManager.isGameRunning()) {
            // Cannot change teams in-game
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Cannot change teams while a game is running").formatted(Formatting.RED), true, false)));
        }

        // Get the team
        var teamName = StringArgumentType.getString(context, "team");

        // Get executing player
        var player = context.getSource().getPlayer();

        // Get team
        var teamDto = teamsConfigState.getTeamOfName(teamName);

        // If team was not found
        if (teamDto.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("That team does not exist.").formatted(Formatting.RED), false, false)));
        }

        // Join team
        var joinSucceeded = teamsManager.playerJoinTeam(player, teamDto.get());

        // If join did not succeed
        if (!joinSucceeded) {
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("That team is already full.").formatted(Formatting.RED), false, false)));
        }

        // Return feedback
        return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("You joined team " + teamName), true, false)));
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("joinTeam")
                .then(argument("team", word())
                        .suggests(TeamSuggestionProvider.getInstance())
                        .executes(new JoinLasertagTeamCommand())));
    }
}

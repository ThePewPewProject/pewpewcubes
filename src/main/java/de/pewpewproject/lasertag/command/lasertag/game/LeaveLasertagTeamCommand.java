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
 * The leave team command
 *
 * @author Étienne Muser
 */
public class LeaveLasertagTeamCommand extends ServerFeedbackCommand {
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();

        // If a game is running
        if (gameManager.isGameRunning()) {
            // Cannot change teams in-game
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Cannot change teams while a game is running").formatted(Formatting.RED), true, false)));
        }

        // Get executing player
        var player = context.getSource().getPlayer();

        // Leave team
        teamsManager.playerLeaveHisTeam(player);

        // Clear inventory
        player.getInventory().clear();

        // Notify player in chat
        return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("You left your team"), true, false)));
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("leaveTeam")
                .executes(new LeaveLasertagTeamCommand()));
    }
}

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
 * The reset team config command
 *
 * @author Étienne Muser
 */
public class ResetTeamConfigCommand extends ServerFeedbackCommand {

    @Override
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var playerNamesState = gameManager.getSyncedState().getPlayerNamesState();
        var syncedState = gameManager.getSyncedState();
        var teamsConfigState = syncedState.getTeamsConfigState();

        // If a game is running
        if (gameManager.isGameRunning()) {
            // Cannot reset teams config in-game
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Cannot reset team config while a game is running").formatted(Formatting.RED), true, false)));
        }

        var world = context.getSource().getWorld();

        // Throw every player out of his team
        playerNamesState.forEachPlayer((playerUuid) -> {
            teamsManager.playerLeaveHisTeam(playerUuid);

            var playerOptional = Optional.ofNullable(world.getPlayerByUuid(playerUuid));
            playerOptional.ifPresent(playerEntity -> playerEntity.getInventory().clear());
        });

        teamsConfigState.reset();

        return CompletableFuture.completedFuture(Optional.empty());
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("resetTeamConfig")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(new ResetTeamConfigCommand()));
    }
}

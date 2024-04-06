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
 * The reload team config command
 *
 * @author Étienne Muser
 */
public class ReloadTeamConfigCommand extends ServerFeedbackCommand {
    @Override
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var playerNamesState = gameManager.getSyncedState().getPlayerNamesState();
        var teamsManager = gameManager.getTeamsManager();

        // If a game is running
        if (gameManager.isGameRunning()) {
            // Cannot reload teams config in-game
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Cannot reload team config while a game is running").formatted(Formatting.RED), true, false)));
        }

        var world = context.getSource().getWorld();

        // Throw every player out of his team
        playerNamesState.forEachPlayer((playerUuid) -> {
            teamsManager.playerLeaveHisTeam(playerUuid);

            var playerOptional = Optional.ofNullable(world.getPlayerByUuid(playerUuid));
            playerOptional.ifPresent(playerEntity -> playerEntity.getInventory().clear());
        });

        teamsManager.reloadTeamsConfig();

        return CompletableFuture.completedFuture(Optional.empty());
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("reloadTeamConfig")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(new ReloadTeamConfigCommand()));
    }
}

package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The reset team config command
 *
 * @author Étienne Muser
 */
public class ResetTeamConfigCommand extends ServerFeedbackCommand {

    @Override
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var playerNamesState = gameManager.getSyncedState().getPlayerNamesState();
        var syncedState = gameManager.getSyncedState();
        var teamsConfigState = syncedState.getTeamsConfigState();

        var world = context.getSource().getWorld();

        // Throw every player out of his team
        playerNamesState.forEachPlayer((playerUuid) -> {
            teamsManager.playerLeaveHisTeam(playerUuid);

            var playerOptional = Optional.ofNullable(world.getPlayerByUuid(playerUuid));
            playerOptional.ifPresent(playerEntity -> playerEntity.getInventory().clear());
        });

        teamsConfigState.reset();

        return Optional.empty();
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("resetTeamConfig")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(new ResetTeamConfigCommand()));
    }
}

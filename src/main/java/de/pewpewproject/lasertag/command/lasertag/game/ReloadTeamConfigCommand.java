package de.pewpewproject.lasertag.command.lasertag.game;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The reload team config command
 *
 * @author Étienne Muser
 */
public class ReloadTeamConfigCommand extends ServerFeedbackCommand {
    @Override
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var playerNamesState = gameManager.getSyncedState().getPlayerNamesState();
        var teamsManager = gameManager.getTeamsManager();

        var world = context.getSource().getWorld();

        // Throw every player out of his team
        playerNamesState.forEachPlayer((playerUuid) -> {
            teamsManager.playerLeaveHisTeam(playerUuid);

            var playerOptional = Optional.ofNullable(world.getPlayerByUuid(playerUuid));
            playerOptional.ifPresent(playerEntity -> playerEntity.getInventory().clear());
        });

        teamsManager.reloadTeamsConfig();

        return Optional.empty();
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("reloadTeamConfig")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(new ReloadTeamConfigCommand()));
    }
}

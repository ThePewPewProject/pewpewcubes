package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
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

        var world = context.getSource().getWorld();
        var teamManager = LasertagGameManager.getInstance().getTeamManager();

        // Throw every player out of his team
        teamManager.forEachPlayer((team, playerUuid) -> {
            teamManager.playerLeaveHisTeam(world, playerUuid);

            var playerOptional = Optional.ofNullable(world.getPlayerByUuid(playerUuid));
            playerOptional.ifPresent(playerEntity -> playerEntity.getInventory().clear());
        });

        LasertagGameManager.getInstance().reloadTeamConfig(world);

        return Optional.empty();
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("reloadTeamConfig")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(new ReloadTeamConfigCommand()));
    }
}

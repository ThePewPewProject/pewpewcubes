package de.pewpewproject.lasertag.command.lasertag.game;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Optional;

import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Command to kick a player out of his team
 *
 * @author Étienne Muser
 */
public class KickPlayerCommand extends ServerFeedbackCommand {

    @Override
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();

        Collection<ServerPlayerEntity> players;
        try {
            players = EntityArgumentType.getPlayers(context, "players");
        } catch (CommandSyntaxException e) {
            return Optional.of(new CommandFeedback(Text.literal("Invalid players").formatted(Formatting.RED), false, false));
        }

        for (var player : players) {
            teamsManager.playerLeaveHisTeam(player);

            player.getInventory().clear();
        }

        // Send successful feedback
        if (players.size() > 1) {
            return Optional.of(new CommandFeedback(Text.literal("Successfully kicked players from their teams."), false, true));
        } else {
            return Optional.of(new CommandFeedback(Text.literal("Successfully kicked player from his team."), false, true));
        }
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("kickPlayer")
                .requires(s -> s.hasPermissionLevel(1))
                .then(argument("players", players())
                        .executes(new KickPlayerCommand())));
    }
}

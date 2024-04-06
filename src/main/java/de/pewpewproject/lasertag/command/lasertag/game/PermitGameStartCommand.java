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
import java.util.concurrent.CompletableFuture;

import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Command to grant a player the ability to start a game
 *
 * @author Étienne Muser
 */
public class PermitGameStartCommand extends ServerFeedbackCommand {

    @Override
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        Collection<ServerPlayerEntity> players;
        try {
            players = EntityArgumentType.getPlayers(context, "players");
        } catch (CommandSyntaxException e) {
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Invalid players").formatted(Formatting.RED), false, false)));
        }

        // Get the server
        var server = context.getSource().getServer();

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var startGamePermissionManager = gameManager.getStartGamePermissionManager();

        // Get the servers player manager
        var playerManager = server.getPlayerManager();

        players.forEach(p -> {
            startGamePermissionManager.setStartGamePermitted(p);
            playerManager.sendCommandTree(p);
        });

        return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Players permitted."), false, false)));
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("permitGameStart")
                .requires(s -> s.hasPermissionLevel(4))
                .then(argument("players", players())
                        .executes(new PermitGameStartCommand())));
    }
}

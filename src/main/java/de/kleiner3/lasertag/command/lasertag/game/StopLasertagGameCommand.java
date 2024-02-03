package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The stop lasertag game command
 *
 * @author Étienne Muser
 */
public class StopLasertagGameCommand extends ServerFeedbackCommand {
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();

        gameManager.stopLasertagGame();

        return Optional.empty();
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("stopLasertagGame")
                .requires(s -> s.hasPermissionLevel(1))
                .executes(new StopLasertagGameCommand()));
    }
}

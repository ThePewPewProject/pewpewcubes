package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The leave team command
 *
 * @author Étienne Muser
 */
public class LeaveLasertagTeamCommand extends ServerFeedbackCommand {
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {
        // Get the server
        var server = context.getSource().getServer();

        // Get executing player
        var player = context.getSource().getPlayer();

        // Leave team
        LasertagGameManager.getInstance().getTeamManager().playerLeaveHisTeam(server.getOverworld(), player);

        // Clear inventory
        player.getInventory().clear();

        // Notify player in chat
        return Optional.of(new CommandFeedback(Text.literal("You left your team"), true, false));
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("leaveTeam")
                .executes(new LeaveLasertagTeamCommand()));
    }
}

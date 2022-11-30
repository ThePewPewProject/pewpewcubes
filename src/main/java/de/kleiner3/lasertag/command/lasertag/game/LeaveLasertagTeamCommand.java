package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The leave team command
 *
 * @author Étienne Muser
 */
public class LeaveLasertagTeamCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        // Get the server
        var server = context.getSource().getServer();

        // Get executing player
        var player = context.getSource().getPlayer();

        // Leave team
        server.playerLeaveHisTeam(player);

        // Clear inventory
        player.getInventory().clear();

        // Notify player in chat
        player.sendMessage(Text.literal("You left your team"), true);

        return Command.SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("leaveTeam")
                .executes(LeaveLasertagTeamCommand::execute));
    }
}

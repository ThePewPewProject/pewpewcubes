package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.suggestions.TeamSuggestionProvider;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The join lasertag team command
 *
 * @author Étiennne Muser
 */
public class JoinLasertagTeamCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        // Get the team
        var teamName = StringArgumentType.getString(context, "team");

        // Get the server
        var server = context.getSource().getServer();

        // Get executing player
        var player = context.getSource().getPlayer();

        // Get team
        var teamDto =  LasertagGameManager.getInstance().getTeamManager().teamConfig.get(teamName);

        // Join team
        server.playerJoinTeam(teamDto, player);

        // Notify player in chat
        player.sendMessage(Text.literal("You joined team " + teamName), true);

        return Command.SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("joinTeam")
                .then(argument("team", word())
                        .suggests(TeamSuggestionProvider.getInstance())
                        .executes(JoinLasertagTeamCommand::execute)));
    }
}

package de.pewpewproject.lasertag.command.lasertag;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.mightypork.rpack.utils.DesktopApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;

/**
 * Command to open the game statistics
 *
 * @author Étienne Muser
 */
public class StatsCommand {

    private static int execute(CommandContext<FabricClientCommandSource> context) {

        // Get the client
        var client = MinecraftClient.getInstance();

        if (client == null) {
            return -1;
        }

        // Get the stats file path
        var statsFilePathOptional = client.getStatsFilePath();

        // If there is no stats file path
        if (statsFilePathOptional.isEmpty()) {

            // Send response
            context.getSource().getPlayer().sendMessage(Text.translatable("chat.message.no_game_stats")
                    .formatted(Formatting.RED));

            return -1;
        }

        // Open the file if present
        DesktopApi.open(new File(statsFilePathOptional.get()));

        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        var cmd = LiteralArgumentBuilder.<FabricClientCommandSource>literal("stats")
                .executes(StatsCommand::execute);

        dispatcher.register(cmd);
    }
}

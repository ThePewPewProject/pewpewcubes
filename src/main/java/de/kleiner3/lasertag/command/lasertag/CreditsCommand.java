package de.kleiner3.lasertag.command.lasertag;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.client.screen.LasertagCreditsScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The command to display the credits for our mod
 *
 * @author Étienne Muser
 */
public class CreditsCommand {
    private static int execute(CommandContext<FabricClientCommandSource> ignoredContext) {
        var client = MinecraftClient.getInstance();

        if (client == null) {
            return -1;
        }

        // Workaround: setScreen(null) in ChatScreen gets triggered after command execution
        Executors.newSingleThreadScheduledExecutor().schedule(() -> client.execute(() -> client.setScreen(new LasertagCreditsScreen())), 250, TimeUnit.MILLISECONDS);

        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        var cmd = LiteralArgumentBuilder.<FabricClientCommandSource>literal("lasertagCredits")
                .executes(CreditsCommand::execute);

        dispatcher.register(cmd);
    }
}

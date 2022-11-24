package de.kleiner3.lasertag.command.lasertag;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.client.screen.LasertagCreditsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

/**
 * The command to display the credits for our mod
 *
 * @author Étienne Muser
 */
public class CreditsCommand {
    private static int execute(CommandContext<ClientCommandSource> ignoredContext) {
        var client = MinecraftClient.getInstance();

        if (client == null) {
            return -1;
        }

        // Workaround: setScreen(null) in ChatScreen gets triggered after command execution
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }

            client.execute(() -> client.setScreen(new LasertagCreditsScreen()));
        }).start();

        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher dispatcher) {
        var cmd = LiteralArgumentBuilder.<ClientCommandSource>literal("lasertagCredits")
                .executes(CreditsCommand::execute);

        dispatcher.register(cmd);
    }
}

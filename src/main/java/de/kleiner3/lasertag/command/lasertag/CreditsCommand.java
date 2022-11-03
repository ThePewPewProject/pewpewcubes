package de.kleiner3.lasertag.command.lasertag;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.client.screen.LasertagCreditsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;

/**
 * The command to display the credits for our mod
 *
 * @author Étienne Muser
 */
public class CreditsCommand {
    private static int execute(CommandContext<ClientCommandSource> context) {
            var client = MinecraftClient.getInstance();

            if (client == null) {
                return Command.SINGLE_SUCCESS;
            }

            // Workaround: setScreen(null) in ChatScreen gets triggered after command execution
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

                client.execute(() -> client.setScreen(new LasertagCreditsScreen()));
            }).start();

        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher dispatcher) {
        var cmd = LiteralArgumentBuilder.<ClientCommandSource>literal("lasertagCredits")
                .executes(ctx -> execute(ctx));

        dispatcher.register(cmd);
    }
}

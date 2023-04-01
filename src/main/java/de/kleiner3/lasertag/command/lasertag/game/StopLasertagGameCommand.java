package de.kleiner3.lasertag.command.lasertag.game;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The stop lasertag game command
 *
 * @author Étienne Muser
 */
public class StopLasertagGameCommand {
    /**
     * Execute the stop lasertag command
     *
     * @param context The CommandContext
     * @return Return code
     */
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        try {
            context.getSource().getServer().stopLasertagGame();
        } catch (Exception ex) {
            LasertagMod.LOGGER.error("An Error occurred while trying to execute that command:", ex);
        }

        return Command.SINGLE_SUCCESS;
    }

    static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("stopLasertagGame")
                .requires(s -> s.hasPermissionLevel(1))
                .executes(StopLasertagGameCommand::execute));
    }
}

package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.lasertaggame.settings.LasertagSettingsManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The reset lasertag settings command
 *
 * @author Étienne Muser
 */
public class ResetSettingsCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        LasertagSettingsManager.reset();

        return Command.SINGLE_SUCCESS;
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("settingsReset")
                .executes(ResetSettingsCommand::execute));
    }
}

package de.kleiner3.lasertag.command.lasertag.game.settings;

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
 * The reset lasertag settings command
 *
 * @author Étienne Muser
 */
public class ResetSettingsCommand extends ServerFeedbackCommand {
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {
        LasertagGameManager.getInstance().getSettingsManager().reset();

        return Optional.of(new CommandFeedback(Text.literal("Settings are reset."), false, true));
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        lab.then(literal("settingsReset")
                .executes(new ResetSettingsCommand()));
    }
}

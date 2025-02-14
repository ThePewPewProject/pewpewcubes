package de.pewpewproject.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Class representing a setting command of a long type setting
 *
 * @author Étienne Muser
 */
public class LongSettingCommand extends ServerFeedbackCommand {

    private final String settingName;
    private final String settingValueName;

    public LongSettingCommand(String settingName, String settingValueName) {
        this.settingName = settingName;
        this.settingValueName = settingValueName;
    }

    @Override
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();

        // If a game is running
        if (gameManager.isGameRunning()) {
            // Cannot change settings in-game
            return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Cannot change settings while a game is running").formatted(Formatting.RED), true, false)));
        }

        var value = LongArgumentType.getLong(context, settingValueName);
        settingsManager.set(settingName, value);

        return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Lasertag setting " + settingName + " is now set to " + value), false, true)));
    }
}

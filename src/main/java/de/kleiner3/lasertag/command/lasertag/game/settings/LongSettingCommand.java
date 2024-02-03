package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.command.CommandFeedback;
import de.kleiner3.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

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
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();

        var value = LongArgumentType.getLong(context, settingValueName);
        settingsManager.set(settingName, value);

        return Optional.of(new CommandFeedback(Text.literal("Lasertag setting " + settingName + " is now set to " + value), false, true));
    }
}

package de.pewpewproject.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.pewpewproject.lasertag.command.CommandFeedback;
import de.pewpewproject.lasertag.command.ServerFeedbackCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Class representing a setting command of a enum type setting
 *
 * @author Étienne Muser
 */
public class EnumSettingCommand extends ServerFeedbackCommand {

    private final String settingName;
    private final String settingValueName;

    public EnumSettingCommand(String settingName, String settingValueName) {
        this.settingName = settingName;
        this.settingValueName = settingValueName;
    }

    @Override
    protected CompletableFuture<Optional<CommandFeedback>> execute(CommandContext<ServerCommandSource> context) {

        // Get the game managers
        var gameManager = context.getSource().getWorld().getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();

        var value = StringArgumentType.getString(context, this.settingValueName);
        settingsManager.set(settingName, value);

        return CompletableFuture.completedFuture(Optional.of(new CommandFeedback(Text.literal("Lasertag setting " + settingName + " is now set to " + value), false, true)));
    }
}

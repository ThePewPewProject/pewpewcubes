package de.pewpewproject.lasertag.command.lasertag.game;

import com.mojang.brigadier.CommandDispatcher;
import de.pewpewproject.lasertag.command.lasertag.game.presets.LasertagSettingsPresetCommand;
import de.pewpewproject.lasertag.command.lasertag.game.settings.LasertagSettingsCommand;
import de.pewpewproject.lasertag.command.lasertag.game.settings.ResetSettingsCommand;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag command
 *
 * @author Étienne Muser
 */
public class LasertagCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var cmd = literal("lasertag");

        StartLasertagGameCommand.register(cmd);
        StopLasertagGameCommand.register(cmd);
        JoinLasertagTeamCommand.register(cmd);
        LeaveLasertagTeamCommand.register(cmd);
        LasertagSettingsCommand.register(cmd);
        ResetSettingsCommand.register(cmd);
        KickPlayerCommand.register(cmd);
        ReloadTeamConfigCommand.register(cmd);
        ResetTeamConfigCommand.register(cmd);
        LasertagSettingsPresetCommand.register(cmd);
        LoadMapCommand.register(cmd);
        LasertagGameModeCommand.register(cmd);

        dispatcher.register(cmd);
    }
}

package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.LasertagConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag settings command
 *
 * @author Étienne Muser
 */
public class LasertagSettingsCommand {
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(Text.literal(new GsonBuilder().setPrettyPrinting().create().toJson(LasertagConfig.getInstance())), false);
        return Command.SINGLE_SUCCESS;
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        var cmd = literal("settings")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(LasertagSettingsCommand::execute);

        RenderTeamListSettingCommand.register(cmd);
        RenderTimerSettingCommand.register(cmd);
        LasertagGameDurationSettingCommand.register(cmd);
        LasertargetHitScoreSettingCommand.register(cmd);
        LasertagPlayerHitScoreSettingCommand.register(cmd);
        ShowLaserRaysSettingCommand.register(cmd);
        PreLasertagGameCooldownSettingCommand.register(cmd);
        PlayerDeactivationDurationSettingCommand.register(cmd);
        LasertargetDeactivationDurationSettingCommand.register(cmd);
        LasertagWeaponUseCooldownSettingCommand.register(cmd);
        LasertagWeaponReachSettingCommand.register(cmd);
        // TODO: statistic file commands

        lab.then(cmd);
    }
}

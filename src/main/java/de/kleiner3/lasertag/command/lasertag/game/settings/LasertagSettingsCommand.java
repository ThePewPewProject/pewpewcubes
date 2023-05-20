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
 * The lasertag settings command
 *
 * @author Étienne Muser
 */
public class LasertagSettingsCommand extends ServerFeedbackCommand {
    protected Optional<CommandFeedback> execute(CommandContext<ServerCommandSource> context) {
        return Optional.of(new CommandFeedback(Text.literal(LasertagGameManager.getInstance().getSettingsManager().toJson()), false, false));
    }

    public static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
        var cmd = literal("settings")
                .requires(s -> s.hasPermissionLevel(4))
                .executes(new LasertagSettingsCommand());

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
        GenerateStatisticsFileSettingCommand.register(cmd);
        AutoOpenStatisticsFileSettingCommand.register(cmd);
        OriginSpawnSettingCommand.register(cmd);
        DeathPenaltySettingCommand.register(cmd);

        lab.then(cmd);
    }
}

package de.kleiner3.lasertag.command.lasertag.game.settings;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDataType;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Builds the command for every lasertag setting
 *
 * @author Étienne Muser
 */
public class SettingsCommandBuilder {

    /**
     * Adds all lasertag setting commands to an existing LiteralArgumentBuilder
     *
     * @param cmd The LiteralArgumentBuilder to add to
     */
    public static void buildSettingsCommands(LiteralArgumentBuilder<ServerCommandSource> cmd) {

        for (var setting : SettingDescription.values()) {
            addSetting(cmd, setting);
        }
    }

    private static void addSetting(LiteralArgumentBuilder<ServerCommandSource> cmd, SettingDescription setting) {

        if (setting.getDataType() == SettingDataType.LONG) {

            buildLongSetting(cmd, setting);
        } else if (setting.getDataType() == SettingDataType.BOOL) {

            buildBoolSetting(cmd, setting);
        } else {

            throw new IllegalArgumentException("DataType " + setting.getDataType().name() + " is not supported by SettingsCommandBuilder.");
        }
    }

    private static void buildLongSetting(LiteralArgumentBuilder<ServerCommandSource> cmd, SettingDescription setting) {

        LongArgumentType argumentType;
        var settingMinValue = (Long)setting.getMinValue();
        var settingMaxValue = (Long)setting.getMaxValue();

        if (settingMinValue != null && settingMaxValue != null) {

            argumentType = longArg(settingMinValue, settingMaxValue);
        } else if (settingMinValue != null) {

            argumentType = longArg(settingMinValue);
        } else {

            argumentType = longArg();
        }

        var command = new LongSettingCommand(setting.getName(), setting.getSettingValueName());

        cmd.then(literal(setting.getName())
                .then(argument(setting.getSettingValueName(), argumentType)
                        .executes(command)));
    }

    private static void buildBoolSetting(LiteralArgumentBuilder<ServerCommandSource> cmd, SettingDescription setting) {

        BoolArgumentType argumentType = bool();
        var command = new BoolSettingCommand(setting.getName(), setting.getSettingValueName());

        cmd.then(literal(setting.getName())
                .then(argument(setting.getSettingValueName(), argumentType)
                        .executes(command)));
    }
}

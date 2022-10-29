package de.kleiner3.lasertag.command.lasertag.game;

import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.kleiner3.lasertag.LasertagConfig;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.command.suggestions.TeamSuggestionProvider;
import de.kleiner3.lasertag.item.LasertagVestItem;
import de.kleiner3.lasertag.item.LasertagWeaponItem;
import de.kleiner3.lasertag.types.Colors;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LasertagCommand {
    private static int execute(CommandContext<ServerCommandSource> context) {
        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher dispatcher) {
        var cmd = literal("lasertag")
                .executes(ctx -> execute(ctx));

        //RenderHudSetting.register(cmd);
        StartGame.register(cmd);
        JoinTeam.register(cmd);
        LeaveTeam.register(cmd);
        LasertagSettings.register(cmd);

        dispatcher.register(cmd);
    }

    private class LasertagSettings {
        private static int execute(CommandContext<ServerCommandSource> context) {
            context.getSource().sendFeedback(Text.literal(new GsonBuilder().setPrettyPrinting().create().toJson(LasertagConfig.getInstance())), false);
            return Command.SINGLE_SUCCESS;
        }
        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            var cmd = literal("settings")
                    .requires(s -> s.hasPermissionLevel(4))
                    .executes(ctx -> execute(ctx));

            RenderTeamListSetting.register(cmd);
            RenderTimerSetting.register(cmd);
            GameDurationSetting.register(cmd);
            LasertargetHitScoreSetting.register(cmd);
            PlayerHitScoreSetting.register(cmd);
            ShowLaserRaysSetting.register(cmd);
            PreGameCooldownSetting.register(cmd);
            PlayerDeactivateDurationSetting.register(cmd);
            LasertargetDeactivateDurationSetting.register(cmd);
            LasertagWeaponUseCooldownSetting.register(cmd);
            LasertagWeaponReachSetting.register(cmd);

            lab.then(cmd);
        }

        private class RenderTeamListSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setRenderTeamList(context.getSource().getServer(), BoolArgumentType.getBool(context, "value"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("renderTeamList")
                        .then(argument("value", bool())
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class RenderTimerSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setRenderTimer(context.getSource().getServer(), BoolArgumentType.getBool(context, "value"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("renderTimer")
                        .then(argument("value", bool())
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class GameDurationSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setPlayTime(context.getSource().getServer(), IntegerArgumentType.getInteger(context, "duration"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("gameDuration")
                        .then(argument("duration", integer(1))
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class LasertargetHitScoreSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setLasertargetHitScore(context.getSource().getServer(), IntegerArgumentType.getInteger(context, "score"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("lasertargetHitScore")
                        .then(argument("score", integer())
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class PlayerHitScoreSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setPlayerHitScore(context.getSource().getServer(), IntegerArgumentType.getInteger(context, "score"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("playerHitScore")
                        .then(argument("score", integer())
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class ShowLaserRaysSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setShowLaserRays(context.getSource().getServer(), BoolArgumentType.getBool(context, "value"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("showLaserRays")
                        .then(argument("value", bool())
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class PreGameCooldownSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setStartTime(context.getSource().getServer(), IntegerArgumentType.getInteger(context, "duration"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("preGameCountdownDuration")
                        .then(argument("duration", integer(0))
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class PlayerDeactivateDurationSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setDeactivateTime(context.getSource().getServer(), IntegerArgumentType.getInteger(context, "duration"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("playerDeactivateDuration")
                        .then(argument("duration", integer(0))
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class LasertargetDeactivateDurationSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setLasertargetDeactivatedTime(context.getSource().getServer(), IntegerArgumentType.getInteger(context, "duration"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("lasertargetDeactivateDuration")
                        .then(argument("duration", integer(0))
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class LasertagWeaponUseCooldownSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setLasertagWeaponCooldown(context.getSource().getServer(), IntegerArgumentType.getInteger(context, "ticks"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("lasertagWeaponUseCooldown")
                        .then(argument("ticks", integer(0))
                                .executes(ctx -> execute(ctx))));
            }
        }

        private class LasertagWeaponReachSetting {
            private static int execute(CommandContext<ServerCommandSource> context) {
                LasertagConfig.getInstance().setLasertagWeaponReach(context.getSource().getServer(), IntegerArgumentType.getInteger(context, "distance"));
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("lasertagWeaponReach")
                        .then(argument("distance", integer(0))
                                .executes(ctx -> execute(ctx))));
            }
        }
    }

    private class StartGame {
        /**
         * Execute the start lasertag command
         *
         * @param context
         * @param scanSpawnpoints
         * @return
         */
        private static int execute(CommandContext<ServerCommandSource> context, boolean scanSpawnpoints) {
            context.getSource().getServer().startGame(scanSpawnpoints);
            return Command.SINGLE_SUCCESS;
        }

        /**
         * Execute the start lasertag command without searching for spawnpoint blocks
         *
         * @param context
         * @return
         */
        private static int execute(CommandContext<ServerCommandSource> context) {
            return execute(context, false);
        }

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            lab.then(literal("startLasertagGame")
                    .requires(s -> s.hasPermissionLevel(1))
                    .executes(ctx -> execute(ctx))
                    .then(literal("scanSpawnpoints")
                            .executes(ctx -> execute(ctx, true))));
        }
    }

    private class JoinTeam {
        private static int execute(CommandContext<ServerCommandSource> context) {
            // Get the team
            var teamName = StringArgumentType.getString(context, "team");

            // Get the server
            var server = context.getSource().getServer();

            // Get executing player
            var player = context.getSource().getPlayer();

            // Get team color
            var teamColor = Colors.colorConfig.get(teamName);

            // Join team
            server.playerJoinTeam(teamColor, player);

            // Get players inventory
            var inventory = player .getInventory();

            // Clear players inventory
            inventory.clear();

            // Give player a lasertag vest
            var vestStack = new ItemStack(LasertagMod.LASERTAG_VEST);
            ((LasertagVestItem)LasertagMod.LASERTAG_VEST).setColor(vestStack, teamColor.getValue());
            player.equipStack(EquipmentSlot.CHEST, vestStack);

            // Give player a lasertag weapon
            var weaponStack = new ItemStack(LasertagMod.LASERTAG_WEAPON);
            ((LasertagWeaponItem)LasertagMod.LASERTAG_WEAPON).setColor(weaponStack, teamColor.getValue());
            inventory.setStack(0, weaponStack);

            return Command.SINGLE_SUCCESS;
        }

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            lab.then(literal("joinTeam")
                    .then(argument("team", word())
                            .suggests(TeamSuggestionProvider.getInstance())
                            .executes(ctx -> execute(ctx))));
        }
    }

    private class LeaveTeam {
        private static int execute(CommandContext<ServerCommandSource> context) {
            // Get the server
            var server = context.getSource().getServer();

            // Get executing player
            var player = context.getSource().getPlayer();

            // Leave team
            server.playerLeaveHisTeam(player);

            // Clear inventory
            player.getInventory().clear();

            return Command.SINGLE_SUCCESS;
        }

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            lab.then(literal("leaveTeam")
                    .executes(ctx -> execute(ctx)));
        }
    }
}

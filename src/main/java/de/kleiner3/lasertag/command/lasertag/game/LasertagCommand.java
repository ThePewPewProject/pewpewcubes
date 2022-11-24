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
import de.kleiner3.lasertag.command.suggestions.TeamSuggestionProvider;
import de.kleiner3.lasertag.types.Colors;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * The lasertag command
 *
 * @author Étienne Muser
 */
public class LasertagCommand {
    // TODO: Dont allow starting a game or switching/leaving teams while a game is running
    @SuppressWarnings("SameReturnValue")
    private static int execute(CommandContext<ServerCommandSource> context) {
        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher dispatcher) {
        var cmd = literal("lasertag")
                .executes(LasertagCommand::execute);

        StartGame.register(cmd);
        JoinTeam.register(cmd);
        LeaveTeam.register(cmd);
        LasertagSettings.register(cmd);

        dispatcher.register(cmd);
    }

    private static class LasertagSettings {
        @SuppressWarnings("SameReturnValue")
        private static int execute(CommandContext<ServerCommandSource> context) {
            context.getSource().sendFeedback(Text.literal(new GsonBuilder().setPrettyPrinting().create().toJson(LasertagConfig.getInstance())), false);
            return Command.SINGLE_SUCCESS;
        }

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            var cmd = literal("settings")
                    .requires(s -> s.hasPermissionLevel(4))
                    .executes(LasertagSettings::execute);

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
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = BoolArgumentType.getBool(context, "value");
                LasertagConfig.getInstance().setRenderTeamList(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting renderTeamList is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("renderTeamList")
                        .then(argument("value", bool())
                                .executes(RenderTeamListSetting::execute)));
            }
        }

        private class RenderTimerSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = BoolArgumentType.getBool(context, "value");
                LasertagConfig.getInstance().setRenderTimer(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting renderTimer is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("renderTimer")
                        .then(argument("value", bool())
                                .executes(RenderTimerSetting::execute)));
            }
        }

        private class GameDurationSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = IntegerArgumentType.getInteger(context, "duration");
                LasertagConfig.getInstance().setPlayTime(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting gameDuration is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("gameDuration")
                        .then(argument("duration", integer(1))
                                .executes(GameDurationSetting::execute)));
            }
        }

        private class LasertargetHitScoreSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = IntegerArgumentType.getInteger(context, "score");
                LasertagConfig.getInstance().setLasertargetHitScore(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting lasertargetHitScore is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("lasertargetHitScore")
                        .then(argument("score", integer())
                                .executes(LasertargetHitScoreSetting::execute)));
            }
        }

        private class PlayerHitScoreSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = IntegerArgumentType.getInteger(context, "score");
                LasertagConfig.getInstance().setPlayerHitScore(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting playerHitScore is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("playerHitScore")
                        .then(argument("score", integer())
                                .executes(PlayerHitScoreSetting::execute)));
            }
        }

        private class ShowLaserRaysSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = BoolArgumentType.getBool(context, "value");
                LasertagConfig.getInstance().setShowLaserRays(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting showLaserRays is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("showLaserRays")
                        .then(argument("value", bool())
                                .executes(ShowLaserRaysSetting::execute)));
            }
        }

        private class PreGameCooldownSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = IntegerArgumentType.getInteger(context, "duration");
                LasertagConfig.getInstance().setStartTime(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting preGameCooldown is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("preGameCountdownDuration")
                        .then(argument("duration", integer(0))
                                .executes(PreGameCooldownSetting::execute)));
            }
        }

        private class PlayerDeactivateDurationSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = IntegerArgumentType.getInteger(context, "duration");
                LasertagConfig.getInstance().setDeactivateTime(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting playerDeactivationDuration is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("playerDeactivateDuration")
                        .then(argument("duration", integer(0))
                                .executes(PlayerDeactivateDurationSetting::execute)));
            }
        }

        private class LasertargetDeactivateDurationSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = IntegerArgumentType.getInteger(context, "duration");
                LasertagConfig.getInstance().setLasertargetDeactivatedTime(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting lasertargetDeactivatedDuration is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("lasertargetDeactivateDuration")
                        .then(argument("duration", integer(0))
                                .executes(LasertargetDeactivateDurationSetting::execute)));
            }
        }

        private class LasertagWeaponUseCooldownSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = IntegerArgumentType.getInteger(context, "ticks");
                LasertagConfig.getInstance().setLasertagWeaponCooldown(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting lasertagWeaponCooldown is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("lasertagWeaponUseCooldown")
                        .then(argument("ticks", integer(0))
                                .executes(LasertagWeaponUseCooldownSetting::execute)));
            }
        }

        @SuppressWarnings("SameReturnValue")
        private class LasertagWeaponReachSetting {
            @SuppressWarnings("SameReturnValue")
            private static int execute(CommandContext<ServerCommandSource> context) {
                var value = IntegerArgumentType.getInteger(context, "distance");
                LasertagConfig.getInstance().setLasertagWeaponReach(context.getSource().getServer(), value);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lasertag setting lasertagWeaponReach is now set to " + value), false);
                return Command.SINGLE_SUCCESS;
            }

            private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
                lab.then(literal("lasertagWeaponReach")
                        .then(argument("distance", integer(0))
                                .executes(LasertagWeaponReachSetting::execute)));
            }
        }
    }

    private static class StartGame {
        /**
         * Execute the start lasertag command
         *
         * @param context
         * @param scanSpawnpoints
         * @return
         */
        @SuppressWarnings("SameReturnValue")
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
                    .executes(StartGame::execute)
                    .then(literal("scanSpawnpoints")
                            .executes(ctx -> execute(ctx, true))));
        }
    }

    private static class JoinTeam {
        @SuppressWarnings("SameReturnValue")
        private static int execute(CommandContext<ServerCommandSource> context) {
            // TODO: Add error messages when team not found

            // Get the team
            var teamName = StringArgumentType.getString(context, "team");

            // Get the server
            var server = context.getSource().getServer();

            // Get executing player
            var player = context.getSource().getPlayer();

            // Get team color
            var teamColor = Colors.colorConfig.get(teamName);

            // Join team
            // TODO: Give error message when team is full
            server.playerJoinTeam(teamColor, player);

            // Notify player in chat
            player.sendMessage(Text.literal("You joined team " + teamName), true);

            return Command.SINGLE_SUCCESS;
        }

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            lab.then(literal("joinTeam")
                    .then(argument("team", word())
                            .suggests(TeamSuggestionProvider.getInstance())
                            .executes(JoinTeam::execute)));
        }
    }

    private static class LeaveTeam {
        @SuppressWarnings("SameReturnValue")
        private static int execute(CommandContext<ServerCommandSource> context) {
            // Get the server
            var server = context.getSource().getServer();

            // Get executing player
            var player = context.getSource().getPlayer();

            // Leave team
            server.playerLeaveHisTeam(player);

            // Clear inventory
            player.getInventory().clear();

            // Notify player in chat
            player.sendMessage(Text.literal("You left your team"), true);

            return Command.SINGLE_SUCCESS;
        }

        private static void register(LiteralArgumentBuilder<ServerCommandSource> lab) {
            lab.then(literal("leaveTeam")
                    .executes(LeaveTeam::execute));
        }
    }
}

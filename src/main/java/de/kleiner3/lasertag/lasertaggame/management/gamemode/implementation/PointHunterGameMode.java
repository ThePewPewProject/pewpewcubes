package de.kleiner3.lasertag.lasertaggame.management.gamemode.implementation;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.common.util.ThreadUtil;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.GameMode;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingDescription;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.TimeUnit;

/**
 * The default game mode. Point hunter. Team-based, time-limited game mode. You earn points for your team
 * by hitting lasertargets or players of other teams. The team with the most points wins.
 *
 * @author Étienne Muser
 */
public class PointHunterGameMode extends GameMode {
    public PointHunterGameMode() {
        super("gameMode.point_hunter", false, true);
    }

    @Override
    public void onTick(MinecraftServer server) {
        // On tick not used in this game mode
    }

    @Override
    public void onPlayerHitLasertarget(MinecraftServer server, ServerPlayerEntity shooter, LaserTargetBlockEntity target) {

        // Get the old block state
        var oldBlockState = server.getOverworld().getBlockState(target.getPos());

        LasertagGameManager.getInstance().getScoreManager().onPlayerScored(server.getOverworld(), shooter, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.LASERTARGET_HIT_SCORE));
        ServerEventSending.sendPlayerSoundEvent(shooter, NetworkingConstants.PLAY_PLAYER_SCORED_SOUND);

        // Deactivate
        target.setDeactivated(true);

        // Reactivate after configured amount of seconds
        var deactivationThread = ThreadUtil.createScheduledExecutor("lasertag-target-deactivation-thread-%d");
        deactivationThread.schedule(() -> {

            // Get the old block state
            var oldBlockStateReset = server.getOverworld().getBlockState(target.getPos());

            target.setDeactivated(false);

            // Get the new block state
            var newBlockState = server.getOverworld().getBlockState(target.getPos());

            // Send lasertag updated to clients
            server.getOverworld().updateListeners(target.getPos(), oldBlockStateReset, newBlockState, Block.NOTIFY_LISTENERS);

            deactivationThread.shutdownNow();
        }, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.LASERTARGET_DEACTIVATE_TIME), TimeUnit.SECONDS);

        // Add player to the players who hit the target
        target.addHitBy(shooter);

        // Get the new block state
        var newBlockState = server.getOverworld().getBlockState(target.getPos());

        // Send lasertag updated to clients
        server.getOverworld().updateListeners(target.getPos(), oldBlockState, newBlockState, Block.NOTIFY_LISTENERS);
    }

    @Override
    public void onPlayerHitPlayer(MinecraftServer server, ServerPlayerEntity shooter, ServerPlayerEntity target) {

        var teamManager = LasertagGameManager.getInstance().getTeamManager();

        var shooterTeam = teamManager.getTeamOfPlayer(shooter.getUuid());
        var targetTeam = teamManager.getTeamOfPlayer(target.getUuid());

        // Check that hit player is not in same team as firing player
        if (shooterTeam.equals(targetTeam)) {
            return;
        }

        // Check if player is deactivated
        if (LasertagGameManager.getInstance().getDeactivatedManager().isDeactivated(target.getUuid())) {
            return;
        }

        var world = server.getOverworld();

        // Deactivate player
        LasertagGameManager.getInstance().getDeactivatedManager().deactivate(target.getUuid(), world, server.getPlayerManager());

        LasertagGameManager.getInstance().getScoreManager().onPlayerScored(world, shooter, LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingDescription.PLAYER_HIT_SCORE));
        ServerEventSending.sendPlayerSoundEvent(shooter, NetworkingConstants.PLAY_PLAYER_SCORED_SOUND);
    }
}

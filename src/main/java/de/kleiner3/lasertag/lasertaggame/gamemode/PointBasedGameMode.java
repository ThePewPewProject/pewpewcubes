package de.kleiner3.lasertag.lasertaggame.gamemode;

import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import de.kleiner3.lasertag.lasertaggame.settings.SettingDescription;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

/**
 * Intermediate class implementing stuff common to all point based
 * game modes.
 *
 * @author Étienne Muser
 */
public abstract class PointBasedGameMode extends GameMode {
    public PointBasedGameMode(String translatableName,
                              boolean infiniteTime,
                              boolean teamsActive) {
        super(translatableName, infiniteTime, teamsActive, false);
    }

    @Override
    public List<SettingDescription> getRelevantSettings() {
        var list = super.getRelevantSettings();

        list.add(SettingDescription.LASERTARGET_HIT_SCORE);
        list.add(SettingDescription.PLAYER_HIT_SCORE);
        list.add(SettingDescription.DEATH_PENALTY);

        return list;
    }

    @Override
    public void onPreGameStart(MinecraftServer server) {
        super.onPreGameStart(server);

        // Reset scores
        server.getOverworld().getServerLasertagManager().getScoreManager().resetScores();
    }

    @Override
    public void onPlayerHitLasertarget(MinecraftServer server, ServerPlayerEntity shooter, LaserTargetBlockEntity target) {

        // Get the managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var scoreManager = gameManager.getScoreManager();
        var settingsManager = gameManager.getSettingsManager();

        scoreManager.onPlayerScored(shooter.getUuid(), settingsManager.<Long>get(SettingDescription.LASERTARGET_HIT_SCORE));
        super.onPlayerHitLasertarget(server, shooter, target);
    }

    @Override
    public void onPlayerHitPlayer(MinecraftServer server, ServerPlayerEntity shooter, ServerPlayerEntity target) {

        // Get the managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var activationManager = gameManager.getActivationManager();
        var scoreManager = gameManager.getScoreManager();
        var settingsManager = gameManager.getSettingsManager();

        var shooterTeam = teamsManager.getTeamOfPlayer(shooter.getUuid());
        var targetTeam = teamsManager.getTeamOfPlayer(target.getUuid());

        // Check that hit player is not in same team as firing player
        if (shooterTeam.equals(targetTeam)) {
            return;
        }

        // Check if player is deactivated
        if (activationManager.isDeactivated(target.getUuid())) {
            return;
        }

        // Deactivate player
        activationManager.deactivate(target.getUuid());

        scoreManager.onPlayerScored(shooter.getUuid(), settingsManager.<Long>get(SettingDescription.PLAYER_HIT_SCORE));
        super.onPlayerHitPlayer(server, shooter, target);
    }

    @Override
    public void onPlayerDeath(MinecraftServer server, ServerPlayerEntity player, DamageSource ignored) {

        // Get the game managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();
        var scoreManager = gameManager.getScoreManager();

        // Give the player the death penalty
        var deathPenalty = -settingsManager.<Long>get(SettingDescription.DEATH_PENALTY);
        scoreManager.onPlayerScored(player.getUuid(), deathPenalty);
    }

    @Override
    public Text getTeamScoreText(TeamDto team) {

        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var scoreManager = gameManager.getScoreManager();
        var teamScore = gameManager.getTeamsManager().getPlayersOfTeam(team).stream()
                .mapToLong(scoreManager::getScore).sum();
        return Text.literal(Long.toString(teamScore));
    }

    @Override
    public Text getPlayerScoreText(UUID playerUuid) {
        return Text.literal(Long.toString(MinecraftClient.getInstance().world.getClientLasertagManager().getScoreManager().getScore(playerUuid)));
    }
}

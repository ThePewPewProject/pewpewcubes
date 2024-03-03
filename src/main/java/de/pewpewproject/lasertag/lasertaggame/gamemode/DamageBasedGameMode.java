package de.pewpewproject.lasertag.lasertaggame.gamemode;

import de.pewpewproject.lasertag.block.entity.LaserTargetBlockEntity;
import de.pewpewproject.lasertag.damage.DamageSources;
import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.util.List;

/**
 * Intermediate class implementing stuff common to all damage
 * based game modes.
 *
 * @author Étienne Muser
 */
public abstract class DamageBasedGameMode extends GameMode {


    public DamageBasedGameMode(String translatableName,
                               boolean infiniteTime,
                               boolean teamsActive) {
        super(translatableName, infiniteTime, teamsActive, true);
    }

    @Override
    public List<SettingDescription> getRelevantSettings() {
        var list = super.getRelevantSettings();

        list.add(SettingDescription.LASER_RAY_DAMAGE);
        list.add(SettingDescription.LASERTARGET_HEAL);
        list.add(SettingDescription.PLAYER_RESET_HEAL);

        return list;
    }

    @Override
    public void onPreGameStart(MinecraftServer server) {
        super.onPreGameStart(server);

        // Set players dont regen health
        var gameRules = server.getGameRules();
        gameRules.get(GameRules.NATURAL_REGENERATION).set(false, server);
    }

    @Override
    public void onPlayerHitLasertarget(MinecraftServer server,
                                       ServerPlayerEntity shooter,
                                       LaserTargetBlockEntity target) {

        // Get the managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();

        long healAmount = settingsManager.get(SettingDescription.LASERTARGET_HEAL);

        // Regenerate health
        shooter.heal(healAmount);

        super.onPlayerHitLasertarget(server, shooter, target);
    }

    @Override
    public void onPlayerHitPlayer(MinecraftServer server, ServerPlayerEntity shooter, ServerPlayerEntity target) {

        // Get the managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var teamsManager = gameManager.getTeamsManager();
        var activationManager = gameManager.getActivationManager();

        // Get the teams of shooter and target
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

        // Get the damage amount
        long damageAmount = gameManager.getSettingsManager().get(SettingDescription.LASER_RAY_DAMAGE);

        // Damage the target
        target.damage(DamageSources.laser(shooter), damageAmount);

        super.onPlayerHitPlayer(server, shooter, target);
    }

    @Override
    public void onPlayerDeath(MinecraftServer server, ServerPlayerEntity player, DamageSource source) {

        // Get the managers
        var gameManager = server.getOverworld().getServerLasertagManager();
        var settingsManager = gameManager.getSettingsManager();

        // If the player got damaged by laser
        if (source.name.equals("laser")) {

            // Get the shooter
            var shooter = (ServerPlayerEntity)source.getAttacker();

            // Heal the shooter
            long healAmount = settingsManager.get(SettingDescription.PLAYER_RESET_HEAL);
            shooter.heal(healAmount);
        }
    }

    @Override
    public void onGameEnd(MinecraftServer server) {
        super.onGameEnd(server);

        // Set players regen health
        var gameRules = server.getGameRules();
        gameRules.get(GameRules.NATURAL_REGENERATION).set(true, server);
    }
}

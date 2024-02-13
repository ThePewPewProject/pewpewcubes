package de.kleiner3.lasertag.lasertaggame.management.gamemode.implementation;

import de.kleiner3.lasertag.common.types.ScoreHolding;
import de.kleiner3.lasertag.lasertaggame.management.gamemode.GameMode;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

/**
 * @author Étienne Muser
 */
public class MusicalChairsGameMode extends GameMode {

    public MusicalChairsGameMode() {
        super("gameMode.musical_chairs", true, false, true);
    }

    @Override
    public void onTick(MinecraftServer server) {
        // TODO
    }

    @Override
    public void onPlayerDeath(MinecraftServer server, ServerPlayerEntity player, DamageSource source) {
        // TODO
    }

    @Override
    public int getWinnerTeamId() {
        // TODO
        return 0;
    }

    @Override
    public Text getTeamScoreText(TeamDto team) {
        // TODO
        return null;
    }

    @Override
    public Text getPlayerScoreText(UUID playerUuid) {
        // TODO
        return null;
    }

    @Override
    public ScoreHolding getTeamFinalScore(TeamDto team) {
        // TODO
        return null;
    }

    @Override
    public ScoreHolding getPlayerFinalScore(UUID playerUuid) {
        // TODO
        return null;
    }
}

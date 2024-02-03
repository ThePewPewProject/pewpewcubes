package de.kleiner3.lasertag.lasertaggame.state.management.server.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.server.IMusicManager;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ArenaType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

/**
 * Implementation of IMusicManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class MusicManager implements IMusicManager {
    private final MinecraftServer server;

    private boolean playingThisMinute = false;

    public MusicManager(MinecraftServer server) {
        this.server = server;
    }

    public void playIntro(ArenaType arenaType) {
        server.getOverworld().playSound(null, new BlockPos(0, 0, 0), arenaType.introMusic, SoundCategory.MUSIC, 1.0f, 1.0f);
    }

    public void tick(ArenaType arenaType, boolean isLastMinute) {

        // Do not play music in the last minute
        if (isLastMinute) {
            return;
        }

        if (this.playingThisMinute) {
            server.getOverworld().playSound(null, new BlockPos(0, 0, 0), arenaType.music, SoundCategory.MUSIC, 1.0f, 1.0f);
        }

        this.playingThisMinute = !this.playingThisMinute;
    }

    public void playOutro(ArenaType arenaType) {
        server.getOverworld().playSound(null, new BlockPos(0, 0, 0), arenaType.outroMusic, SoundCategory.MUSIC, 1.0f, 1.0f);
    }

    public void reset() {
        this.playingThisMinute = false;
    }
}

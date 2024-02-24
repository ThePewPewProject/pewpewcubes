package de.kleiner3.lasertag.lasertaggame.state.management.server.implementation;

import de.kleiner3.lasertag.lasertaggame.state.management.server.IMusicManager;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ArenaType;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

/**
 * Implementation of IMusicManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class MusicManager implements IMusicManager {

    private static final float SOUND_VOLUME = 0.3f;

    private final MinecraftServer server;

    private boolean playingThisMinute = false;

    public MusicManager(MinecraftServer server) {
        this.server = server;
    }

    public void playIntro(ArenaType arenaType) {
        server.getOverworld().playSound(null, new BlockPos(0, 0, 0), arenaType.introMusic, SoundCategory.MUSIC, SOUND_VOLUME, 1.0f);
    }

    public void tick(ArenaType arenaType, boolean isLastMinute) {

        // Do not play music in the last minute
        if (isLastMinute) {
            return;
        }

        if (this.playingThisMinute) {
            server.getOverworld().playSound(null, new BlockPos(0, 0, 0), arenaType.music, SoundCategory.MUSIC, SOUND_VOLUME, 1.0f);
        }

        this.playingThisMinute = !this.playingThisMinute;
    }

    public void playOutro(ArenaType arenaType) {
        server.getOverworld().playSound(null, new BlockPos(0, 0, 0), arenaType.outroMusic, SoundCategory.MUSIC, SOUND_VOLUME, 1.0f);
    }

    @Override
    public void stopMusic() {

        // Get all players from the server
        var players = server.getPlayerManager().getPlayerList();

        // Create the stop sound packet
        // null, null to stop all sounds
        var stopSoundPacket = new StopSoundS2CPacket(null, null);

        // Send stop sound packet to every player
        players.forEach(p -> p.networkHandler.sendPacket(stopSoundPacket));
    }

    public void reset() {
        this.playingThisMinute = false;
    }
}

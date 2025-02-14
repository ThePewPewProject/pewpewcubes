package de.pewpewproject.lasertag.lasertaggame.state.management.server.implementation;

import de.pewpewproject.lasertag.lasertaggame.state.management.server.IMusicManager;
import de.pewpewproject.lasertag.worldgen.chunkgen.type.ArenaType;
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

    /**
     * Flag to indicate there should be music running the next minute
     */
    private boolean playingNextMinute = false;

    /**
     * Flag to indicate the music manager is running.
     * If this flag is false, then the music manager doesn't
     * react to tick events.
     * This is preventing a tick after reset to mutate the state
     * of the music manager.
     */
    private boolean isRunning = false;

    public MusicManager(MinecraftServer server) {
        this.server = server;
    }

    public void playIntro(ArenaType arenaType) {

        isRunning = true;
        server.getOverworld().playSound(null, BlockPos.ORIGIN, arenaType.introMusic, SoundCategory.MUSIC, SOUND_VOLUME, 1.0f);
    }

    public void tick(ArenaType arenaType, boolean isLastMinute) {

        // If the music manager is not running
        if (!isRunning) {

            // Do nothing
            return;
        }

        // Do not play music in the last minute
        if (isLastMinute) {
            return;
        }

        if (playingNextMinute) {
            server.getOverworld().playSound(null, BlockPos.ORIGIN, arenaType.music, SoundCategory.MUSIC, SOUND_VOLUME, 1.0f);
        }

        playingNextMinute = !playingNextMinute;
    }

    public void playOutro(ArenaType arenaType) {

        isRunning = false;
        server.getOverworld().playSound(null, BlockPos.ORIGIN, arenaType.outroMusic, SoundCategory.MUSIC, SOUND_VOLUME, 1.0f);
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

        isRunning = false;
        playingNextMinute = false;
    }
}

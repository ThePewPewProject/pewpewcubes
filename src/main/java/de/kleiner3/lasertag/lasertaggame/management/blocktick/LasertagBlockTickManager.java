package de.kleiner3.lasertag.lasertaggame.management.blocktick;

import de.kleiner3.lasertag.block.entity.LasertagCustomBlockTickable;
import de.kleiner3.lasertag.lasertaggame.management.IManager;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the custom lasertag block ticks. This a workaround for the weird behaviour observed when reloading the arena.
 *
 * @author Étienne Muser
 */
public class LasertagBlockTickManager implements IManager {

    private final List<LasertagCustomBlockTickable> blockEntityTickers;
    private final MinecraftServer server;

    public LasertagBlockTickManager(MinecraftServer server) {
        this.blockEntityTickers = new ArrayList<>();
        this.server = server;
    }

    public void registerTicker(LasertagCustomBlockTickable ticker) {
        blockEntityTickers.add(ticker);
    }

    public void clear() {
        this.blockEntityTickers.clear();
    }

    public void tick() {
        blockEntityTickers.forEach(ticker -> ticker.serverTick(this.server.getOverworld()));
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }
}

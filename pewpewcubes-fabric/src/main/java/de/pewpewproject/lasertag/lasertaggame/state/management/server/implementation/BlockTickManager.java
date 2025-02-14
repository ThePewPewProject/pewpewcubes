package de.pewpewproject.lasertag.lasertaggame.state.management.server.implementation;

import de.pewpewproject.lasertag.block.entity.LasertagCustomBlockTickable;
import de.pewpewproject.lasertag.lasertaggame.state.management.server.IBlockTickManager;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of IBlockTickManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class BlockTickManager implements IBlockTickManager {
    private final Set<LasertagCustomBlockTickable> blockEntityTickers;
    private final MinecraftServer server;

    public BlockTickManager(MinecraftServer server) {
        this.blockEntityTickers = new HashSet<>();
        this.server = server;
    }

    @Override
    public synchronized void registerTicker(LasertagCustomBlockTickable ticker) {
        blockEntityTickers.add(ticker);
    }

    @Override
    public void unregisterTicker(LasertagCustomBlockTickable ticker) {
        blockEntityTickers.remove(ticker);
    }

    @Override
    public synchronized void clear() {
        this.blockEntityTickers.clear();
    }

    @Override
    public synchronized void tick() {
        blockEntityTickers.forEach(ticker -> ticker.serverTick(this.server.getOverworld()));
    }
}

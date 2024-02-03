package de.kleiner3.lasertag.lasertaggame.state.management.server.implementation;

import de.kleiner3.lasertag.block.entity.LasertagCustomBlockTickable;
import de.kleiner3.lasertag.lasertaggame.state.management.server.IBlockTickManager;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of IBlockTickManager for the server lasertag game
 *
 * @author Étienne Muser
 */
public class BlockTickManager implements IBlockTickManager {
    private final List<LasertagCustomBlockTickable> blockEntityTickers;
    private final MinecraftServer server;

    public BlockTickManager(MinecraftServer server) {
        this.blockEntityTickers = new ArrayList<>();
        this.server = server;
    }

    public synchronized void registerTicker(LasertagCustomBlockTickable ticker) {
        blockEntityTickers.add(ticker);
    }

    public synchronized void clear() {
        this.blockEntityTickers.clear();
    }

    public synchronized void tick() {
        blockEntityTickers.forEach(ticker -> ticker.serverTick(this.server.getOverworld()));
    }
}

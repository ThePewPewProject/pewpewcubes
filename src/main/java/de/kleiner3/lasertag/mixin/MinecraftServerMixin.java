package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.lasertaggame.ILasertagServerManagerAccessor;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.LasertagServerManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Interface injection into MinecraftServer to implement the lasertag game
 *
 * @author Étienne Muser
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ILasertagServerManagerAccessor {

    private LasertagServerManager lasertagServerManager;

    @Shadow
    public abstract PlayerManager getPlayerManager();

    @Shadow
    public abstract ServerWorld getOverworld();

    /**
     * Inject into constructor of MinecraftServer
     *
     * @param ci The CallbackInfo
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        lasertagServerManager = new LasertagServerManager((MinecraftServer) (Object) this);
    }

    /**
     * Inject into the stop method of the minecraft server.
     * This method gets called after entering the /stop command or typing stop into the server console.
     *
     * @param ci
     */
    @Inject(method = "shutdown", at = @At("HEAD"))
    private void atShutdown(CallbackInfo ci) {

        // Stop the lasertag game
        lasertagServerManager.stopLasertagGame();

        // Dispose the game managers
        LasertagGameManager.getInstance().dispose();
    }

    @Override
    public LasertagServerManager getLasertagServerManager() {
        return lasertagServerManager;
    }
}
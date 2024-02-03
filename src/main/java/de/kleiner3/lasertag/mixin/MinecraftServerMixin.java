package de.kleiner3.lasertag.mixin;

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
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract PlayerManager getPlayerManager();

    @Shadow
    public abstract ServerWorld getOverworld();

    /**
     * Inject into the stop method of the minecraft server.
     * This method gets called after entering the /stop command or typing stop into the server console.
     *
     * @param ci
     */
    @Inject(method = "shutdown", at = @At("HEAD"))
    private void atShutdown(CallbackInfo ci) {

        // Get the game managers
        var gameManager = getOverworld().getServerLasertagManager();

        // Stop the lasertag game
        gameManager.stopLasertagGame();

        // Dispose the game managers
        gameManager.dispose();
    }
}
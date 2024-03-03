package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.lasertaggame.IStatsFilePathHolding;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

/**
 * Mixin into the MinecraftClient.class to inject the IStatsFilePathHolding interface
 *
 * @author Étienne Muser
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IStatsFilePathHolding {

    private String statsFilePath = null;

    @Override
    public void setStatsFilePath(String newPath) {
        statsFilePath = newPath;
    }

    @Override
    public Optional<String> getStatsFilePath() {
        return Optional.ofNullable(statsFilePath);
    }
}

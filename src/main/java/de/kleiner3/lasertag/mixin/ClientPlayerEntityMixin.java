package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.client.screen.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Mixin into the ClientPlayerEntity.class to implement the lasertag game manager screen opener
 *
 * @author Étienne Muser
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements ILasertagGameManagerScreenOpener, ILasertagTeamSelectorScreenOpener, ILasertagCreditsScreenOpener {
    @Shadow @Final protected MinecraftClient client;

    @Override
    public void openLasertagGameManagerScreen(PlayerEntity player) {
        this.client.setScreen(new LasertagGameManagerScreen(player));
    }

    @Override
    public void openLasertagTeamSelectorScreen() {
        this.client.setScreen(new LasertagTeamSelectorScreen());
    }

    @Override
    public void openLasertagCreditsScreen(PlayerEntity player) {
        this.client.setScreen(new LasertagCreditsScreen());
    }
}

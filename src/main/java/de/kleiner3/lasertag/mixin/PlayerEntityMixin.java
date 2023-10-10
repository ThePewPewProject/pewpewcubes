package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.block.entity.LasertagTeamZoneGeneratorBlockEntity;
import de.kleiner3.lasertag.client.screen.ILasertagCreditsScreenOpener;
import de.kleiner3.lasertag.client.screen.ILasertagGameManagerScreenOpener;
import de.kleiner3.lasertag.client.screen.ILasertagTeamSelectorScreenOpener;
import de.kleiner3.lasertag.client.screen.ILasertagTeamZoneGeneratorScreenOpener;
import de.kleiner3.lasertag.lasertaggame.ILasertagPlayer;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.networking.server.ServerEventSending;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin into the PlayerEntity class to implement the ILasertagPlayer
 *
 * @author Étienne Muser
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ILasertagPlayer, ILasertagGameManagerScreenOpener, ILasertagTeamSelectorScreenOpener, ILasertagCreditsScreenOpener, ILasertagTeamZoneGeneratorScreenOpener {

    @Override
    public void onDeactivated() {

        // Play deactivation sound
        ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity)(Object)this, NetworkingConstants.PLAY_PLAYER_DEACTIVATED_SOUND);
    }

    @Override
    public void onActivated() {

        // Play activation sound
        ServerEventSending.sendPlayerSoundEvent((ServerPlayerEntity)(Object)this, NetworkingConstants.PLAY_PLAYER_ACTIVATED_SOUND);
    }

    @Override
    public String getLasertagUsername() {
        return ((PlayerEntity)(Object)this).getDisplayName().getString();
    }

    @Override
    public void openLasertagGameManagerScreen(PlayerEntity player) {
        // Do nothing - may not be a client player
    }

    @Override
    public void openLasertagTeamSelectorScreen(PlayerEntity player) {
        // Do nothing - may not be a client player
    }

    @Override
    public void openLasertagCreditsScreen(PlayerEntity player) {
        // Do nothing - may not be a client player
    }

    @Override
    public void openLasertagTeamZoneGeneratorScreen(LasertagTeamZoneGeneratorBlockEntity teamZoneGenerator) {
        // Do nothing - may not be a client player
    }
}

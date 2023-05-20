package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.settings.SettingNames;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into the ServerPlayerEntity.class
 *
 * @author Étienne Muser
 */
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onPlayerDeath(DamageSource damageSource, CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity)(Object)this);

        // Get the server
        MinecraftServer server = player.getServer();
        if (server != null) {
            var deathPenalty = -LasertagGameManager.getInstance().getSettingsManager().<Long>get(SettingNames.DEATH_PENALTY);
            server.onPlayerScored(player, deathPenalty);
        }
    }
}

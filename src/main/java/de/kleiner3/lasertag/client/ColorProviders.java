package de.kleiner3.lasertag.client;

import de.kleiner3.lasertag.block.Blocks;
import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

/**
 * Class for registering all color providers
 *
 * @author Étienne Muser
 */
public class ColorProviders {
    public static void register() {
        // Register color provider for lasertag weapon
        ColorProviderRegistry.ITEM.register((stack, tintIdx) -> {

            var holder = stack.getHolder();

            if (holder == null) {
                return 0x000000;
            }

            var holderUuid = holder.getUuid();

            var deactivated = LasertagGameManager.getInstance().getDeactivatedManager().isDeactivated(holderUuid);
            if (deactivated) {
                return 0x000000;
            }

            var team = LasertagGameManager.getInstance().getTeamManager().getTeamOfPlayer(holderUuid);
            return team.map(teamDto -> teamDto.color().getValue()).orElse(0xFFFFFF);

        }, Items.LASERTAG_WEAPON);

        // Register color provider for lasertarget block
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            return 0xFF0000;
        }, Blocks.LASER_TARGET);
    }
}

package de.kleiner3.lasertag;

import de.kleiner3.lasertag.client.LasertagHudOverlay;
import de.kleiner3.lasertag.command.ClientCommandInitializer;
import de.kleiner3.lasertag.entity.render.LaserRayEntityRenderer;
import de.kleiner3.lasertag.entity.render.armor.LasertagVestRenderer;
import de.kleiner3.lasertag.networking.client.ClientNetworkingHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

/**
 * Initializes the client side of the mod
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public class LasertagModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // ===== Register entity renderers ====================
        EntityRendererRegistry.register(LasertagMod.LASER_RAY, (ctx) -> new LaserRayEntityRenderer(ctx));

        // ===== Register packet recievers ====================
        ClientNetworkingHandler clientNetworkingHandler = new ClientNetworkingHandler();
        clientNetworkingHandler.register();

        // ===== Register HUD Overlay =========================
        HudRenderCallback.EVENT.register(new LasertagHudOverlay());

        // ===== Register color providers =====================
        ColorProviderRegistry.ITEM.register((stack, tintIdx) -> {
            // Team color
            if (stack.hasNbt()) {
                var nbt = stack.getNbt();

                if (nbt.contains("deactivated") && nbt.getBoolean("deactivated")) {
                    return 0x000000;
                }
                if (nbt.contains("color")) {
                    return nbt.getInt("color");
                }
            }

            return 0xFFFFFF;
        }, LasertagMod.LASERTAG_WEAPON);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            // Team color
            if (stack.hasNbt()) {
                return stack.getNbt().getInt("color");
            }

            return 0xFFFFFF;
        }, LasertagMod.LASERTAG_VEST);

        // ===== Register GeckoLib renderers ===================
        GeoArmorRenderer.registerArmorRenderer(new LasertagVestRenderer(), LasertagMod.LASERTAG_VEST);

        // ===== Register commands =====================
        ClientCommandInitializer.initCommands();
    }

}

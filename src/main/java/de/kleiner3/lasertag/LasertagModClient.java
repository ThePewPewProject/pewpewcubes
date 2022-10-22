package de.kleiner3.lasertag;

import de.kleiner3.lasertag.client.LasertagHudOverlay;
import de.kleiner3.lasertag.entity.render.LaserRayEntityRenderer;
import de.kleiner3.lasertag.networking.client.ClientNetworkingHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

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
        EntityRendererRegistry.register(LasertagMod.LASER_RAY, (ctx) -> {
            return new LaserRayEntityRenderer(ctx);
        });

        // ===== Register packet recievers ====================
        ClientNetworkingHandler clientNetworkingHandler = new ClientNetworkingHandler();
        clientNetworkingHandler.register();

        // ===== Register HUD Overlay =========================
        HudRenderCallback.EVENT.register(new LasertagHudOverlay());

        // ===== Register color providers =====================
        ColorProviderRegistry.ITEM.register((stack, tintIdx) ->  {
            if (stack.hasNbt()) {
                return stack.getNbt().getInt("color");
            }

            return 0xFFFFFF;
        }, LasertagMod.LASERTAG_WEAPON);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (stack.hasNbt()) {
                return stack.getNbt().getInt("color");
            }

            return 0xFFFFFF;
        }, LasertagMod.LASERTAG_VEST);
    }

}

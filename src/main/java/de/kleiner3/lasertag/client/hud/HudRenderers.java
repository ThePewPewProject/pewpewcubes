package de.kleiner3.lasertag.client.hud;

import de.kleiner3.lasertag.client.hud.LasertagHudOverlay;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * Class for registering all HUD renderers
 *
 * @author Étienne Muser
 */
public class HudRenderers {
    public static void register() {
        HudRenderCallback.EVENT.register(new LasertagHudOverlay());
    }
}

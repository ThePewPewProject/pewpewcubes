package de.pewpewproject.lasertag.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Class for holding and registering key bindings
 *
 * @author Étienne Muser
 */
public class KeyBindings {

    private static KeyBinding weaponZoomKeyBinding;

    public static void register() {

        weaponZoomKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lasertag.weapon_zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "keybind.category.lasertag"));
    }

    public static boolean isWeaponZoomPressed() {
        return weaponZoomKeyBinding.isPressed();
    }
}

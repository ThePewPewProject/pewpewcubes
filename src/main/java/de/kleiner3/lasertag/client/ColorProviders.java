package de.kleiner3.lasertag.client;

import de.kleiner3.lasertag.item.Items;
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
        }, Items.LASERTAG_WEAPON);

        // Register color provider for lasertag vest
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            // Team color
            if (stack.hasNbt()) {
                return stack.getNbt().getInt("color");
            }

            return 0xFFFFFF;
        }, Items.LASERTAG_VEST);
    }
}

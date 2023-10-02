package de.kleiner3.lasertag.worldgen;

import de.kleiner3.lasertag.LasertagMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.WorldPreset;

/**
 * Class holding all world presets
 *
 * @author Étienne Muser
 */
public class WorldPresets {
    public static final RegistryKey<WorldPreset> ARENA = RegistryKey.of(Registry.WORLD_PRESET_KEY, new Identifier(LasertagMod.ID, "lasertag_arena"));
}

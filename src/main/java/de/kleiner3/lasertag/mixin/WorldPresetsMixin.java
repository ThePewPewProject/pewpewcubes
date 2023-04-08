package de.kleiner3.lasertag.mixin;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
import de.kleiner3.lasertag.worldgen.chunkgen.ArenaChunkGeneratorConfig;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldPresets.Registrar.class)
public abstract class WorldPresetsMixin {

    private static final RegistryKey<WorldPreset> ARENA = RegistryKey.of(Registry.WORLD_PRESET_KEY, new Identifier(LasertagMod.ID, "lasertag_arena"));

    @Shadow
    protected abstract RegistryEntry<WorldPreset> register(RegistryKey<WorldPreset> key, DimensionOptions dimensionOptions);
    @Shadow protected abstract DimensionOptions createOverworldOptions(ChunkGenerator chunkGenerator);


    @Inject(method = "initAndGetDefault", at = @At("RETURN"))
    private void addPresets(CallbackInfoReturnable<RegistryEntry<WorldPreset>> cir) {
        this.register(ARENA, this.createOverworldOptions(new ArenaChunkGenerator(BuiltinRegistries.STRUCTURE_SET, BuiltinRegistries.BIOME, ArenaChunkGeneratorConfig.getDefaultConfig())));
    }
}

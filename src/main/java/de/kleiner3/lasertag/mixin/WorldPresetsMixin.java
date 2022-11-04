package de.kleiner3.lasertag.mixin;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(WorldPresets.Registrar.class)
public abstract class WorldPresetsMixin {
    private static final RegistryKey<WorldPreset> VOID_WORLD = RegistryKey.of(Registry.WORLD_PRESET_KEY, new Identifier("lasertag", "void_world"));
    @Shadow
    protected abstract RegistryEntry<WorldPreset> register(RegistryKey<WorldPreset> key, DimensionOptions dimensionOptions);
    @Shadow protected abstract DimensionOptions createOverworldOptions(ChunkGenerator chunkGenerator);

    @Inject(method = "initAndGetDefault", at = @At("RETURN"))
    private void addPresets(CallbackInfoReturnable<RegistryEntry<WorldPreset>> cir) {
        // the register() method is shadowed from the target class
        this.register(VOID_WORLD, this.createOverworldOptions(
                        // a FlatChunkGenerator is the easiest way to get a void world, but you can replace this FlatChunkGenerator constructor with a NoiseChunkGenerator, or your own custom ChunkGenerator.
                        new FlatChunkGenerator(
                                // passing null will use the default structure set
                                null,
                                new FlatChunkGeneratorConfig(
                                        // we don't need to overwrite the structure set
                                        Optional.empty(),
                                        BuiltinRegistries.BIOME)
                        )
                )
        );
    }
}

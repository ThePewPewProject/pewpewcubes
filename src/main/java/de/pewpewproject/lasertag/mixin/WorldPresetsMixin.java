package de.pewpewproject.lasertag.mixin;

import de.pewpewproject.lasertag.worldgen.chunkgen.ArenaChunkGenerator;
import de.pewpewproject.lasertag.worldgen.chunkgen.ArenaChunkGeneratorConfig;
import net.minecraft.util.registry.BuiltinRegistries;
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

    @Shadow
    protected abstract RegistryEntry<WorldPreset> register(RegistryKey<WorldPreset> key, DimensionOptions dimensionOptions);
    @Shadow protected abstract DimensionOptions createOverworldOptions(ChunkGenerator chunkGenerator);


    @Inject(method = "initAndGetDefault", at = @At("RETURN"))
    private void addPresets(CallbackInfoReturnable<RegistryEntry<WorldPreset>> cir) {
        this.register(de.pewpewproject.lasertag.worldgen.WorldPresets.ARENA,
                this.createOverworldOptions(
                        new ArenaChunkGenerator(BuiltinRegistries.STRUCTURE_SET,
                                BuiltinRegistries.BIOME,
                                ArenaChunkGeneratorConfig.getDefaultConfig())));
    }
}

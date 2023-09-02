package de.kleiner3.lasertag.worldgen.chunkgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
/**
 * Class to hold the arena chunk generator configuration data
 *
 * @author Étienne Muser
 */
public class ArenaChunkGeneratorConfig {
    private int type;

    public static final Codec<ArenaChunkGeneratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                Codec.INT.fieldOf("type")
                        .orElse(0)
                        .forGetter((arenaChunkGeneratorConfig) -> arenaChunkGeneratorConfig.type))
                        .apply(instance, ArenaChunkGeneratorConfig::new));

    public ArenaChunkGeneratorConfig(int type) {
        this.type = type;
    }

    public ArenaType getType() {
        return ArenaType.values()[type];
    }

    public void setType(int type) {
        this.type = type;
    }

    public static ArenaChunkGeneratorConfig getDefaultConfig() {
        return new ArenaChunkGeneratorConfig(0);
    }
}

package de.pewpewproject.lasertag.worldgen.chunkgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.pewpewproject.lasertag.worldgen.chunkgen.type.ArenaType;
import de.pewpewproject.lasertag.worldgen.chunkgen.type.ProceduralArenaType;

/**
 * Class to hold the arena chunk generator configuration data
 *
 * @author Étienne Muser
 */
public class ArenaChunkGeneratorConfig {
    private int type;
    private int proceduralType;
    private long seed;

    public static final Codec<ArenaChunkGeneratorConfig> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                Codec.INT.fieldOf("type")
                        .orElse(0)
                        .forGetter(arenaChunkGeneratorConfig -> arenaChunkGeneratorConfig.type),
                Codec.INT.fieldOf("proceduralType")
                        .orElse(0)
                        .forGetter(arenaChunkGeneratorConfig -> arenaChunkGeneratorConfig.proceduralType),
                Codec.LONG.fieldOf("seed")
                        .orElse(0L)
                        .forGetter(arenaChunkGeneratorConfig -> arenaChunkGeneratorConfig.seed))
                    .apply(instance, ArenaChunkGeneratorConfig::new));

    public ArenaChunkGeneratorConfig(int type, int proceduralType, long seed) {
        this.type = type;
        this.proceduralType = proceduralType;
        this.seed = seed;
    }

    public ArenaType getType() {
        return ArenaType.values()[type];
    }

    public void setType(int type) {
        this.type = type;
    }

    public ProceduralArenaType getProceduralType() { return ProceduralArenaType.values()[proceduralType]; }

    public void setProceduralType(int type) {
        this.proceduralType = type;
    }

    public long getSeed() { return this.seed; }

    public void setSeed(long seed) { this.seed = seed; }

    public static ArenaChunkGeneratorConfig getDefaultConfig() {
        return new ArenaChunkGeneratorConfig(0, 0, 0L);
    }
}

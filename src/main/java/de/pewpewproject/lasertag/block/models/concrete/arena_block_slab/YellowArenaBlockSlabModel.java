package de.pewpewproject.lasertag.block.models.concrete.arena_block_slab;

import de.pewpewproject.lasertag.block.models.EmissiveSlabModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class YellowArenaBlockSlabModel extends EmissiveSlabModel {
    public YellowArenaBlockSlabModel() {
        super(
                "block/arena_block_dark",
                "block/yellow_arena_block_glow",
                "block/arena_block_half_dark",
                "block/yellow_arena_block_half_glow"
        );
    }
}

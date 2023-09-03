package de.kleiner3.lasertag.block.models.concrete.arena_block_slab;

import de.kleiner3.lasertag.block.models.EmissiveSlabModel;

public class GreenArenaBlockSlabModel extends EmissiveSlabModel {
    public GreenArenaBlockSlabModel() {
        super(
                "block/arena_block_dark",
                "block/green_arena_block_glow",
                "block/arena_block_half_dark",
                "block/green_arena_block_half_glow"
        );
    }
}

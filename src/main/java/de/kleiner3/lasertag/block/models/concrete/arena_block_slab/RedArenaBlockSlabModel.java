package de.kleiner3.lasertag.block.models.concrete.arena_block_slab;

import de.kleiner3.lasertag.block.models.EmissiveSlabModel;

public class RedArenaBlockSlabModel extends EmissiveSlabModel {
    public RedArenaBlockSlabModel() {
        super(
                "block/arena_block_dark",
                "block/red_arena_block_glow",
                "block/arena_block_half_dark",
                "block/red_arena_block_half_glow"
        );
    }
}

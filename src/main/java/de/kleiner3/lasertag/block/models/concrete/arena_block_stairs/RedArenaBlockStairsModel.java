package de.kleiner3.lasertag.block.models.concrete.arena_block_stairs;

import de.kleiner3.lasertag.block.models.EmissiveStairsModel;

public class RedArenaBlockStairsModel extends EmissiveStairsModel {
    public RedArenaBlockStairsModel() {
        super(
                "block/arena_block_dark",
                "block/red_arena_block_glow",
                "block/arena_block_half_dark",
                "block/red_arena_block_half_glow",
                "block/arena_block_stair_side_dark",
                "block/red_arena_block_stair_side_glow"
        );
    }
}

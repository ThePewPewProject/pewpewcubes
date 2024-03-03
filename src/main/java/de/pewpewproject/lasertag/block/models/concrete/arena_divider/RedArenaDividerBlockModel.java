package de.pewpewproject.lasertag.block.models.concrete.arena_divider;

import de.pewpewproject.lasertag.block.models.EmissiveDividerBlockModel;

public class RedArenaDividerBlockModel extends EmissiveDividerBlockModel {
    public RedArenaDividerBlockModel() {
        super(
                "block/arena_block_dark",
                "block/red_arena_block_glow",
                "block/arena_divider_side_dark",
                "block/red_arena_divider_side_glow"
        );
    }
}

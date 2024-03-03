package de.pewpewproject.lasertag.block.models.concrete.arena_divider;

import de.pewpewproject.lasertag.block.models.EmissiveDividerBlockModel;

public class GreenArenaDividerBlockModel extends EmissiveDividerBlockModel {
    public GreenArenaDividerBlockModel() {
        super(
                "block/arena_block_dark",
                "block/green_arena_block_glow",
                "block/arena_divider_side_dark",
                "block/green_arena_divider_side_glow"
        );
    }
}

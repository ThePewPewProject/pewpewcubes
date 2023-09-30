package de.kleiner3.lasertag.block.models.concrete.arena_pillar_block;

import de.kleiner3.lasertag.block.models.EmissivePillarBlockModel;

public class GreenArenaPillarBlockModel extends EmissivePillarBlockModel {
    public GreenArenaPillarBlockModel() {
        super(
                "block/arena_block_dark",
                "block/no_glow",
                "block/arena_block_dark",
                "block/green_arena_block_pillar_glow"
        );
    }
}
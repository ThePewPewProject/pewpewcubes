package de.kleiner3.lasertag.block.models.concrete;

import de.kleiner3.lasertag.block.models.EmissivePillarBlockModel;

/**
 * Custom block model for the arena pillar block
 *
 * @author Étienne Muser
 */
public class ArenaPillarBlockModel extends EmissivePillarBlockModel {
    public ArenaPillarBlockModel() {
        super(
                "block/arena_block_dark",
                "block/no_glow",
                "block/arena_block_pillar",
                "block/arena_block_pillar_glow"
        );
    }
}

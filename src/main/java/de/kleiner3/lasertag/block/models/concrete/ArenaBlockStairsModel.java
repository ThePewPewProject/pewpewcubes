package de.kleiner3.lasertag.block.models.concrete;

import de.kleiner3.lasertag.block.models.EmissiveStairsModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Custom block model for the arena block stairs
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public class ArenaBlockStairsModel extends EmissiveStairsModel {
    public ArenaBlockStairsModel() {
        super(
                "block/arena_block",
                "block/arena_block_glow",
                "block/arena_block_half",
                "block/arena_block_half_glow",
                "block/arena_block_stair_side",
                "block/arena_block_stair_side_glow");
    }
}

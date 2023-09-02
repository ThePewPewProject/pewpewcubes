package de.kleiner3.lasertag.block.models.concrete;

import de.kleiner3.lasertag.block.models.EmissiveSlabModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Custom block model for the arena block slab
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public class ArenaBlockSlabModel extends EmissiveSlabModel {
    public ArenaBlockSlabModel() {
        super(
                "block/arena_block",
                "block/arena_block_glow",
                "block/arena_block_half",
                "block/arena_block_half_glow"
        );
    }
}

package de.kleiner3.lasertag.block.models.concrete;

import de.kleiner3.lasertag.block.models.EmissiveBlockModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Custom block model for the arena block
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public class ArenaBlockModel extends EmissiveBlockModel {

    public ArenaBlockModel() {
        super("block/arena_block", "block/arena_block_glow");
    }
}

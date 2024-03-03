package de.pewpewproject.lasertag.block.models.concrete.arena_block;

import de.pewpewproject.lasertag.block.models.EmissiveBlockModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class YellowArenaBlockModel extends EmissiveBlockModel {

    public YellowArenaBlockModel() {
        super("block/arena_block_dark", "block/yellow_arena_block_glow");
    }
}

package de.kleiner3.lasertag.block.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ArenaBlockModel extends EmissiveBlockModel {

    public ArenaBlockModel() {
        super("block/arena_block", "block/arena_block_glow");
    }
}

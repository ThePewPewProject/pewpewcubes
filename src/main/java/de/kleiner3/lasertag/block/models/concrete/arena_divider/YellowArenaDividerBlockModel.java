package de.kleiner3.lasertag.block.models.concrete.arena_divider;

import de.kleiner3.lasertag.block.models.EmissiveDividerBlockModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class YellowArenaDividerBlockModel extends EmissiveDividerBlockModel {
    public YellowArenaDividerBlockModel() {
        super(
                "block/arena_block_dark",
                "block/yellow_arena_block_glow",
                "block/arena_divider_side_dark",
                "block/yellow_arena_divider_side_glow"
        );
    }
}

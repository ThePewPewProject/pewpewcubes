package de.kleiner3.lasertag.block.models.concrete;

import de.kleiner3.lasertag.block.models.EmissiveDividerBlockModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Custom block model for the arena divider
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public class ArenaDividerBlockModel extends EmissiveDividerBlockModel {
    public ArenaDividerBlockModel() {
        super(
                "block/arena_block",
                "block/arena_block_glow",
                "block/arena_divider_side",
                "block/arena_divider_side_glow"
        );
    }
}

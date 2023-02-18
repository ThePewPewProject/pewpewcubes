package de.kleiner3.lasertag.lasertaggame.teammanagement;

import de.kleiner3.lasertag.common.types.ColorDto;
import net.minecraft.block.Block;


/**
 * Team DTO
 *
 * @author Étienne Muser
 */
public record TeamDto(String name, ColorDto color, Block spawnpointBlock) {
    public TeamDto(String name, int r, int g, int b, Block spawnpointBlock) {
        this(name, new ColorDto(r, g, b), spawnpointBlock);
    }

    /**
     * Checks whether the other team can coexist with this team.
     * Name, color and spawnpoint block must be different
     * @param other
     * @return
     */
    public boolean canCoexistWith(TeamDto other) {
        return !name.equals(other.name) &&
                !color.equals(other.color) &&
                !spawnpointBlock.equals(other.spawnpointBlock);
    }
}

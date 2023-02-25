package de.kleiner3.lasertag.lasertaggame.teammanagement;

import de.kleiner3.lasertag.common.types.ColorDto;
import net.minecraft.block.Block;

import java.util.Objects;


/**
 * Team DTO
 *
 * @author Étienne Muser
 */
public class TeamDto {
    private String name;
    private ColorDto color;
    private Block spawnpointBlock;

    public TeamDto(String name, ColorDto color, Block spawnpointBlock) {
        this.name = name;
        this.color = color;
        this.spawnpointBlock = spawnpointBlock;
    }

    public TeamDto(String name, int r, int g, int b, Block spawnpointBlock) {
        this(name, new ColorDto(r, g, b), spawnpointBlock);
    }

    /**
     * Checks whether the other team can coexist with this team.
     * Name, color and spawnpoint block must be different
     *
     * @param other
     * @return
     */
    public boolean canCoexistWith(TeamDto other) {
        return !name.equals(other.name) &&
                !color.equals(other.color) &&
                !spawnpointBlock.equals(other.spawnpointBlock);
    }

    public String name() {
        return name;
    }

    public ColorDto color() {
        return color;
    }

    public Block spawnpointBlock() {
        return spawnpointBlock;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TeamDto otherTeam) {
            return name.equals(otherTeam.name) &&
                    color.equals(otherTeam.color) &&
                    spawnpointBlock.equals(otherTeam.spawnpointBlock);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, spawnpointBlock);
    }

    @Override
    public String toString() {
        return "Team " + name + " " + color.toString();
    }
}

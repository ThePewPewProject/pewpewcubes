package de.pewpewproject.lasertag.lasertaggame.team;

import de.pewpewproject.lasertag.common.types.ColorDto;
import net.minecraft.block.Block;


/**
 * Team DTO
 *
 * @author Étienne Muser
 */
public class TeamDto {

    private int id;
    private String name;
    private ColorDto color;
    private Block spawnpointBlock;

    public TeamDto(int id, String name, ColorDto color, Block spawnpointBlock) {

        this.id = id;
        this.name = name;
        this.color = color;
        this.spawnpointBlock = spawnpointBlock;
    }

    public TeamDto(int id, String name, int r, int g, int b, Block spawnpointBlock) {
        this(id, name, new ColorDto(r, g, b), spawnpointBlock);
    }

    /**
     * Checks whether the other team can coexist with this team.
     * Name, color and spawnpoint block must be different
     *
     * @param other
     * @return
     */
    public boolean canCoexistWith(TeamDto other) {
        return id != other.id &&
                !name.equals(other.name) &&
                !color.equals(other.color) &&
                !spawnpointBlock.equals(other.spawnpointBlock);
    }

    public int id() { return id; }

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

            return id == otherTeam.id;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Team " + name + " " + color.toString();
    }
}

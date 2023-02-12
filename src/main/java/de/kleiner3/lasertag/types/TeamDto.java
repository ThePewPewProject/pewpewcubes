package de.kleiner3.lasertag.types;

import net.minecraft.block.Block;

import java.util.Objects;

/**
 * Team DTO
 *
 * @author Étienne Muser
 */
public class TeamDto {
    public TeamDto(String name, int r, int g, int b, Block spawnpointBlock) {
        this(name, new ColorDto(r, g, b), spawnpointBlock);
    }

    public TeamDto(String name, ColorDto color, Block spawnpointBlock) {
        this.teamName = name;
        this.color = color;
        this.spawnpointBlock = spawnpointBlock;
    }

    private final String teamName;

    private final ColorDto color;

    private final Block spawnpointBlock;

    public String getName() {
        return teamName;
    }

    public ColorDto getColor() {
        return color;
    }

    /**
     * Get the spawnpoint block type of this team
     * @return The spawnpoint block
     */
    public Block getSpawnpointBlock() {
        return spawnpointBlock;
    }

    @Override
    public String toString() {
        return teamName + " " + color.toString();
    }

    // ===== Auto Generated =====
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        TeamDto teamDto = (TeamDto) o;
        return color.equals(teamDto.color) && teamName.equals(teamDto.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamName, color);
    }
    // ==========================
}

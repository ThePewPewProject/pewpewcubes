package de.kleiner3.lasertag.types;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

/**
 * Enumeration of all available lasertag colors
 *
 * @author Étienne Muser
 */
public enum Colors {
    RED(255, 0, 0, Blocks.RED_CONCRETE),
    GREEN(0, 255, 0, Blocks.LIME_CONCRETE),
    BLUE(0, 0, 255, Blocks.BLUE_CONCRETE),
    ORANGE(255, 128, 0, Blocks.ORANGE_CONCRETE),
    TEAL(0, 128, 255, Blocks.LIGHT_BLUE_CONCRETE),
    PINK(255, 0, 255, Blocks.PINK_CONCRETE);

    Colors(int r, int g, int b, Block spawnpointBlock) {
        this.r = (r & 0xFF);
        this.g = (g & 0xFF);
        this.b = (b & 0xFF);
        this.spawnpointBlock = spawnpointBlock;

        intValue = this.r << 16 | this.g << 8 | this.b;
    }

    private final int intValue;
    private final int r;
    private final int g;
    private final int b;

    private final Block spawnpointBlock;

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public Block getSpawnpointBlock() {
        return spawnpointBlock;
    }

    public int getValue() {
        return intValue;
    }

    public float[] getFloatArray() {
        return new float[]{r / 255.0F, g / 255.0F, b / 255.0F};
    }
}

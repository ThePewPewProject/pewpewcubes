package de.kleiner3.lasertag.types;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Enumeration of all available lasertag colors
 *
 * @author Étienne Muser
 */
public class Colors {
    public static HashMap<String, Color> colorConfig = new HashMap<>();

    static {
        colorConfig.put("Red", new Color("Red", 255, 0, 0, Blocks.RED_CONCRETE));
        colorConfig.put("Green", new Color("Green", 0, 255, 0, Blocks.LIME_CONCRETE));
        colorConfig.put("Blue", new Color("Blue", 0, 0, 255, Blocks.BLUE_CONCRETE));
        colorConfig.put("Orange", new Color("Orange", 255, 128, 0, Blocks.ORANGE_CONCRETE));
        colorConfig.put("Teal", new Color("Teal", 0, 128, 255, Blocks.LIGHT_BLUE_CONCRETE));
        colorConfig.put("Pink", new Color("Pink", 255, 0, 255, Blocks.PINK_CONCRETE));
    }

    public static class Color {
        Color(String name, int r, int g, int b, Block spawnpointBlock) {
            this.name = name;

            this.r = (r & 0xFF);
            this.g = (g & 0xFF);
            this.b = (b & 0xFF);
            this.spawnpointBlock = spawnpointBlock;

            intValue = this.r << 16 | this.g << 8 | this.b;
        }

        private String name;
        private final int intValue;
        private final int r;
        private final int g;
        private final int b;

        private final Block spawnpointBlock;

        public String getName() {
            return name;
        }
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
}

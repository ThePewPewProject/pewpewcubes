package de.kleiner3.lasertag.types;

import com.google.gson.reflect.TypeToken;
import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.util.FileIO;
import de.kleiner3.lasertag.util.serialize.ColorConfigDeserializer;
import de.kleiner3.lasertag.util.serialize.LasertagColorSerializer;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Enumeration of all available lasertag colors
 *
 * @author Étienne Muser
 */
public class Colors {
    /**
     * The path to the team config file
     */
    private static final String colorConfigFilePath = LasertagMod.configFolderPath + "\\teamConfig.json";

    /**
     * The actual in-memory team config
     */
    public static HashMap<String, Color> colorConfig = null;

    static {
        // TODO: Color config leaks over into singleplayer from servers
        // TODO: Catch that teams must be unique
        // TODO: Make color config reloadable by command

        // Get config file
        var colorConfigFile = new File(colorConfigFilePath);

        // If the config file exists
        if (colorConfigFile.exists()) {
            try {
                // Read config file
                var configFileContents = FileIO.readAllFile(colorConfigFile);

                // get gson builder
                var gsonBuilder = ColorConfigDeserializer.getDeserializer();

                // Parse
                colorConfig = gsonBuilder.create().fromJson(configFileContents, new TypeToken<HashMap<String, Color>>() {
                }.getType());
            } catch (IOException ex) {
                LasertagMod.LOGGER.warn("Reading of team config file failed: " + ex.getMessage());
            }
        }

        // If config couldn't be loaded from file
        if (colorConfig == null) {
            // Create map
            colorConfig = new HashMap<>();

            // Fill map with default values
            colorConfig.put("Red", new Color("Red", 255, 0, 0, Blocks.RED_CONCRETE));
            colorConfig.put("Green", new Color("Green", 0, 255, 0, Blocks.LIME_CONCRETE));
            colorConfig.put("Blue", new Color("Blue", 0, 0, 255, Blocks.BLUE_CONCRETE));
            colorConfig.put("Orange", new Color("Orange", 255, 128, 0, Blocks.ORANGE_CONCRETE));
            colorConfig.put("Teal", new Color("Teal", 0, 128, 255, Blocks.LIGHT_BLUE_CONCRETE));
            colorConfig.put("Pink", new Color("Pink", 255, 0, 255, Blocks.PINK_CONCRETE));

            // Get gson builder
            var gsonBuilder = LasertagColorSerializer.getSerializer();

            // Serialize
            var configJson = gsonBuilder.setPrettyPrinting().create().toJson(colorConfig);

            // Persist
            try {
                var dir = new File(LasertagMod.configFolderPath);

                // Create directory if not exists
                if (dir.exists() == false) {
                    dir.mkdir();
                }

                // Create file if not exists
                if (colorConfigFile.exists() == false) {
                    colorConfigFile.createNewFile();
                }

                // Write to file
                FileIO.writeAllFile(colorConfigFile, configJson);
            } catch (IOException e) {
                LasertagMod.LOGGER.error("Writing to team config file failed: " + e.getMessage());
            }
        }
    }

    /**
     * The color class
     */
    public static class Color {
        public Color(String name, int r, int g, int b, Block spawnpointBlock) {
            this.teamName = name;

            this.r = (r & 0xFF);
            this.g = (g & 0xFF);
            this.b = (b & 0xFF);
            this.spawnpointBlock = spawnpointBlock;
        }

        private String teamName;
        private final int r;
        private final int g;
        private final int b;

        private final Block spawnpointBlock;

        public String getName() {
            return teamName;
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

        /**
         * Get the spawnpoint block type of this team
         * @return
         */
        public Block getSpawnpointBlock() {
            return spawnpointBlock;
        }

        /**
         * Get the integer color value with bits distributed like:
         * RRRRRRRRGGGGGGGGBBBBBBBB
         * @return
         */
        public int getValue() {
            return this.r << 16 | this.g << 8 | this.b;
        }

        /**
         * Get the color values as normalized float array
         * @return
         */
        public float[] getFloatArray() {
            return new float[]{r / 255.0F, g / 255.0F, b / 255.0F};
        }

        @Override
        public String toString() {
            return teamName + " (" + r + ", " + g + ", " + b + ")";
        }


        // ===== Auto Generated =====
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Color color = (Color) o;
            return r == color.r && g == color.g && b == color.b && teamName.equals(color.teamName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(teamName, r, g, b);
        }
        // ==========================
    }

    public static void syncTeamsToClient(ServerPlayerEntity player) {
        // Get gson builder
        var gsonBuilder = LasertagColorSerializer.getSerializer();

        // Serialize
        var configJson = gsonBuilder.create().toJson(colorConfig);

        // Create packet buffer
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        // Write errorMessage to buffer
        buf.writeString(configJson);

        // Send to all clients
        ServerPlayNetworking.send(player, NetworkingConstants.LASERTAG_TEAMS_SYNC, buf);
    }
}

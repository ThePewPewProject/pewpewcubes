package de.kleiner3.lasertag.common.util;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Class containing static methods for mapping block ids to block entity ids
 *
 * @author Étienne Muser
 */
public class TileEntityLookup {

    /**
     * Tries to find a block entity id for a given block name
     *
     * @param blockName The name (id - minecraft:xyz) of the block
     * @return The id of the block entity or null if no entry was found
     */
    public static String lookupBlockName(String blockName) {
        return tileEntityIdLookupTable.get(blockName);
    }

    /**
     * Lookup table used to reconstruct the id of tile entities
     * key: The name of the block
     * value: The id of the tile entity
     */
    private static final Map<String, String> tileEntityIdLookupTable = Map.<String, String>ofEntries(
            entry("minecraft:sculk_sensor", "minecraft:sculk_sensor"),
            entry("minecraft:sculk_shrieker", "minecraft:sculk_shrieker"),

            entry("minecraft:chest", "minecraft:chest"),
            entry("minecraft:ender_chest", "minecraft:ender_chest"),
            entry("minecraft:trapped_chest", "minecraft:trapped_chest"),
            entry("minecraft:barrel", "minecraft:barrel"),

            entry("minecraft:campfire", "minecraft:campfire"),
            entry("minecraft:soul_campfire", "minecraft:campfire"),

            entry("lasertag:lasertarget", "lasertag:lasertarget_entity"),

            entry("minecraft:lectern", "minecraft:lectern"),

            entry("minecraft:command_block", "minecraft:command_block"),

            entry("minecraft:brewing_stand", "minecraft:brewing_stand"),

            entry("minecraft:beacon", "minecraft:beacon"),

            entry("minecraft:bell", "minecraft:bell"),

            entry("minecraft:hopper", "minecraft:hopper"),

            entry("minecraft:jukebox", "minecraft:jukebox"),

            entry("minecraft:conduit", "minecraft:conduit"),

            entry("minecraft:comparator", "minecraft:comparator"),

            entry("minecraft:enchanting_table", "minecraft:enchanting_table"),

            entry("minecraft:dispenser", "minecraft:dispenser"),
            entry("minecraft:dropper", "minecraft:dropper"),

            entry("minecraft:bee_nest", "minecraft:beehive"),
            entry("minecraft:beehive", "minecraft:beehive"),

            entry("minecraft:furnace", "minecraft:furnace"),
            entry("minecraft:blast_furnace", "minecraft:blast_furnace"),
            entry("minecraft:smoker", "minecraft:smoker"),

            entry("minecraft:shulker_box", "minecraft:shulker_box"),
            entry("minecraft:red_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:lime_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:pink_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:gray_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:cyan_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:blue_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:white_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:brown_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:green_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:black_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:orange_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:yellow_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:purple_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:magenta_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:light_blue_shulker_box", "minecraft:shulker_box"),
            entry("minecraft:light_gray_shulker_box", "minecraft:shulker_box"),

            entry("minecraft:red_bed", "minecraft:bed"),
            entry("minecraft:lime_bed", "minecraft:bed"),
            entry("minecraft:pink_bed", "minecraft:bed"),
            entry("minecraft:gray_bed", "minecraft:bed"),
            entry("minecraft:cyan_bed", "minecraft:bed"),
            entry("minecraft:blue_bed", "minecraft:bed"),
            entry("minecraft:white_bed", "minecraft:bed"),
            entry("minecraft:brown_bed", "minecraft:bed"),
            entry("minecraft:green_bed", "minecraft:bed"),
            entry("minecraft:black_bed", "minecraft:bed"),
            entry("minecraft:orange_bed", "minecraft:bed"),
            entry("minecraft:yellow_bed", "minecraft:bed"),
            entry("minecraft:purple_bed", "minecraft:bed"),
            entry("minecraft:magenta_bed", "minecraft:bed"),
            entry("minecraft:light_blue_bed", "minecraft:bed"),
            entry("minecraft:light_gray_bed", "minecraft:bed"),

            entry("minecraft:red_banner", "minecraft:banner"),
            entry("minecraft:red_wall_banner", "minecraft:banner"),
            entry("minecraft:lime_banner", "minecraft:banner"),
            entry("minecraft:pink_banner", "minecraft:banner"),
            entry("minecraft:gray_banner", "minecraft:banner"),
            entry("minecraft:cyan_banner", "minecraft:banner"),
            entry("minecraft:blue_banner", "minecraft:banner"),
            entry("minecraft:lime_wall_banner", "minecraft:banner"),
            entry("minecraft:pink_wall_banner", "minecraft:banner"),
            entry("minecraft:gray_wall_banner", "minecraft:banner"),
            entry("minecraft:cyan_wall_banner", "minecraft:banner"),
            entry("minecraft:blue_wall_banner", "minecraft:banner"),
            entry("minecraft:white_banner", "minecraft:banner"),
            entry("minecraft:brown_banner", "minecraft:banner"),
            entry("minecraft:green_banner", "minecraft:banner"),
            entry("minecraft:black_banner", "minecraft:banner"),
            entry("minecraft:white_wall_banner", "minecraft:banner"),
            entry("minecraft:brown_wall_banner", "minecraft:banner"),
            entry("minecraft:green_wall_banner", "minecraft:banner"),
            entry("minecraft:black_wall_banner", "minecraft:banner"),
            entry("minecraft:orange_banner", "minecraft:banner"),
            entry("minecraft:yellow_banner", "minecraft:banner"),
            entry("minecraft:purple_banner", "minecraft:banner"),
            entry("minecraft:orange_wall_banner", "minecraft:banner"),
            entry("minecraft:yellow_wall_banner", "minecraft:banner"),
            entry("minecraft:purple_wall_banner", "minecraft:banner"),
            entry("minecraft:magenta_wall_banner", "minecraft:banner"),
            entry("minecraft:magenta_banner", "minecraft:banner"),
            entry("minecraft:light_blue_wall_banner", "minecraft:banner"),
            entry("minecraft:light_gray_wall_banner", "minecraft:banner"),
            entry("minecraft:light_blue_banner", "minecraft:banner"),
            entry("minecraft:light_gray_banner", "minecraft:banner"),

            entry("minecraft:sign", "minecraft:sign"),
            entry("minecraft:oak_sign", "minecraft:sign"),
            entry("minecraft:wall_sign", "minecraft:sign"),
            entry("minecraft:birch_sign", "minecraft:sign"),
            entry("minecraft:spruce_sign", "minecraft:sign"),
            entry("minecraft:jungle_sign", "minecraft:sign"),
            entry("minecraft:acacia_sign", "minecraft:sign"),
            entry("minecraft:warped_sign", "minecraft:sign"),
            entry("minecraft:dark_oak_sign", "minecraft:sign"),
            entry("minecraft:oak_wall_sign", "minecraft:sign"),
            entry("minecraft:crimson_sign", "minecraft:sign"),
            entry("minecraft:birch_wall_sign", "minecraft:sign"),
            entry("minecraft:spruce_wall_sign", "minecraft:sign"),
            entry("minecraft:acacia_wall_sign", "minecraft:sign"),
            entry("minecraft:jungle_wall_sign", "minecraft:sign"),
            entry("minecraft:warped_wall_sign", "minecraft:sign"),
            entry("minecraft:dark_oak_wall_sign", "minecraft:sign"),
            entry("minecraft:crimson_wall_sign", "minecraft:sign"),

            entry("minecraft:skeleton_skull", "minecraft:skull"),
            entry("minecraft:skeleton_wall_skull", "minecraft:skull"),
            entry("minecraft:wither_skeleton_skull", "minecraft:skull"),
            entry("minecraft:player_head", "minecraft:skull"),
            entry("minecraft:zombie_head", "minecraft:skull"),
            entry("minecraft:dragon_head", "minecraft:skull"),
            entry("minecraft:creeper_head", "minecraft:skull"),
            entry("minecraft:zombie_wall_head", "minecraft:skull"),
            entry("minecraft:player_wall_head", "minecraft:skull"),
            entry("minecraft:dragon_wall_head", "minecraft:skull"),
            entry("minecraft:creeper_wall_head", "minecraft:skull"),
            entry("minecraft:wither_skeleton_wall_skull", "minecraft:skull")
    );
}

package de.pewpewproject.lasertag.block.models;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.block.models.concrete.arena_block.*;
import de.pewpewproject.lasertag.block.models.concrete.arena_block_slab.*;
import de.pewpewproject.lasertag.block.models.concrete.arena_block_stairs.*;
import de.pewpewproject.lasertag.block.models.concrete.arena_divider.*;
import de.pewpewproject.lasertag.block.models.concrete.arena_pillar_block.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Class to provide the custom block models
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public class BlockModelProvider implements ModelResourceProvider {

    public static final Identifier ORANGE_ARENA_BLOCK = new Identifier(LasertagMod.ID, "block/arena_block_orange");
    public static final Identifier PINK_ARENA_BLOCK = new Identifier(LasertagMod.ID, "block/arena_block_pink");
    public static final Identifier BLUE_ARENA_BLOCK = new Identifier(LasertagMod.ID, "block/arena_block_blue");
    public static final Identifier YELLOW_ARENA_BLOCK = new Identifier(LasertagMod.ID, "block/arena_block_yellow");
    public static final Identifier GREEN_ARENA_BLOCK = new Identifier(LasertagMod.ID, "block/arena_block_green");
    public static final Identifier PURPLE_ARENA_BLOCK = new Identifier(LasertagMod.ID, "block/arena_block_purple");
    public static final Identifier RED_ARENA_BLOCK = new Identifier(LasertagMod.ID, "block/arena_block_red");

    public static final Identifier ORANGE_ARENA_PILLAR_BLOCK = new Identifier(LasertagMod.ID, "block/arena_pillar_block_orange");
    public static final Identifier PINK_ARENA_PILLAR_BLOCK = new Identifier(LasertagMod.ID, "block/arena_pillar_block_pink");
    public static final Identifier BLUE_ARENA_PILLAR_BLOCK = new Identifier(LasertagMod.ID, "block/arena_pillar_block_blue");
    public static final Identifier YELLOW_ARENA_PILLAR_BLOCK = new Identifier(LasertagMod.ID, "block/arena_pillar_block_yellow");
    public static final Identifier GREEN_ARENA_PILLAR_BLOCK = new Identifier(LasertagMod.ID, "block/arena_pillar_block_green");
    public static final Identifier PURPLE_ARENA_PILLAR_BLOCK = new Identifier(LasertagMod.ID, "block/arena_pillar_block_purple");
    public static final Identifier RED_ARENA_PILLAR_BLOCK = new Identifier(LasertagMod.ID, "block/arena_pillar_block_red");

    public static final Identifier ORANGE_ARENA_BLOCK_STAIRS = new Identifier(LasertagMod.ID, "block/arena_block_stairs_orange");
    public static final Identifier PINK_ARENA_BLOCK_STAIRS = new Identifier(LasertagMod.ID, "block/arena_block_stairs_pink");
    public static final Identifier BLUE_ARENA_BLOCK_STAIRS = new Identifier(LasertagMod.ID, "block/arena_block_stairs_blue");
    public static final Identifier YELLOW_ARENA_BLOCK_STAIRS = new Identifier(LasertagMod.ID, "block/arena_block_stairs_yellow");
    public static final Identifier GREEN_ARENA_BLOCK_STAIRS = new Identifier(LasertagMod.ID, "block/arena_block_stairs_green");
    public static final Identifier PURPLE_ARENA_BLOCK_STAIRS = new Identifier(LasertagMod.ID, "block/arena_block_stairs_purple");
    public static final Identifier RED_ARENA_BLOCK_STAIRS = new Identifier(LasertagMod.ID, "block/arena_block_stairs_red");

    public static final Identifier ORANGE_ARENA_DIVIDER = new Identifier(LasertagMod.ID, "block/arena_divider_orange");
    public static final Identifier PINK_ARENA_DIVIDER = new Identifier(LasertagMod.ID, "block/arena_divider_pink");
    public static final Identifier BLUE_ARENA_DIVIDER = new Identifier(LasertagMod.ID, "block/arena_divider_blue");
    public static final Identifier YELLOW_ARENA_DIVIDER = new Identifier(LasertagMod.ID, "block/arena_divider_yellow");
    public static final Identifier GREEN_ARENA_DIVIDER = new Identifier(LasertagMod.ID, "block/arena_divider_green");
    public static final Identifier PURPLE_ARENA_DIVIDER = new Identifier(LasertagMod.ID, "block/arena_divider_purple");
    public static final Identifier RED_ARENA_DIVIDER = new Identifier(LasertagMod.ID, "block/arena_divider_red");

    public static final Identifier ORANGE_ARENA_BLOCK_SLAB = new Identifier(LasertagMod.ID, "block/arena_block_slab_orange");
    public static final Identifier PINK_ARENA_BLOCK_SLAB = new Identifier(LasertagMod.ID, "block/arena_block_slab_pink");
    public static final Identifier BLUE_ARENA_BLOCK_SLAB = new Identifier(LasertagMod.ID, "block/arena_block_slab_blue");
    public static final Identifier YELLOW_ARENA_BLOCK_SLAB = new Identifier(LasertagMod.ID, "block/arena_block_slab_yellow");
    public static final Identifier GREEN_ARENA_BLOCK_SLAB = new Identifier(LasertagMod.ID, "block/arena_block_slab_green");
    public static final Identifier PURPLE_ARENA_BLOCK_SLAB = new Identifier(LasertagMod.ID, "block/arena_block_slab_purple");
    public static final Identifier RED_ARENA_BLOCK_SLAB = new Identifier(LasertagMod.ID, "block/arena_block_slab_red");



    public static final Identifier ORANGE_ARENA_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_orange");
    public static final Identifier PINK_ARENA_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_pink");
    public static final Identifier BLUE_ARENA_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_blue");
    public static final Identifier YELLOW_ARENA_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_yellow");
    public static final Identifier GREEN_ARENA_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_green");
    public static final Identifier PURPLE_ARENA_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_purple");
    public static final Identifier RED_ARENA_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_red");

    public static final Identifier ORANGE_ARENA_PILLAR_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_pillar_block_orange");
    public static final Identifier PINK_ARENA_PILLAR_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_pillar_block_pink");
    public static final Identifier BLUE_ARENA_PILLAR_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_pillar_block_blue");
    public static final Identifier YELLOW_ARENA_PILLAR_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_pillar_block_yellow");
    public static final Identifier GREEN_ARENA_PILLAR_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_pillar_block_green");
    public static final Identifier PURPLE_ARENA_PILLAR_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_pillar_block_purple");
    public static final Identifier RED_ARENA_PILLAR_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_pillar_block_red");

    public static final Identifier ORANGE_ARENA_BLOCK_STAIRS_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_stairs_orange");
    public static final Identifier PINK_ARENA_BLOCK_STAIRS_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_stairs_pink");
    public static final Identifier BLUE_ARENA_BLOCK_STAIRS_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_stairs_blue");
    public static final Identifier YELLOW_ARENA_BLOCK_STAIRS_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_stairs_yellow");
    public static final Identifier GREEN_ARENA_BLOCK_STAIRS_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_stairs_green");
    public static final Identifier PURPLE_ARENA_BLOCK_STAIRS_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_stairs_purple");
    public static final Identifier RED_ARENA_BLOCK_STAIRS_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_stairs_red");

    public static final Identifier ORANGE_ARENA_DIVIDER_ITEM = new Identifier(LasertagMod.ID, "item/arena_divider_orange");
    public static final Identifier PINK_ARENA_DIVIDER_ITEM = new Identifier(LasertagMod.ID, "item/arena_divider_pink");
    public static final Identifier BLUE_ARENA_DIVIDER_ITEM = new Identifier(LasertagMod.ID, "item/arena_divider_blue");
    public static final Identifier YELLOW_ARENA_DIVIDER_ITEM = new Identifier(LasertagMod.ID, "item/arena_divider_yellow");
    public static final Identifier GREEN_ARENA_DIVIDER_ITEM = new Identifier(LasertagMod.ID, "item/arena_divider_green");
    public static final Identifier PURPLE_ARENA_DIVIDER_ITEM = new Identifier(LasertagMod.ID, "item/arena_divider_purple");
    public static final Identifier RED_ARENA_DIVIDER_ITEM = new Identifier(LasertagMod.ID, "item/arena_divider_red");

    public static final Identifier ORANGE_ARENA_BLOCK_SLAB_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_slab_orange");
    public static final Identifier PINK_ARENA_BLOCK_SLAB_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_slab_pink");
    public static final Identifier BLUE_ARENA_BLOCK_SLAB_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_slab_blue");
    public static final Identifier YELLOW_ARENA_BLOCK_SLAB_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_slab_yellow");
    public static final Identifier GREEN_ARENA_BLOCK_SLAB_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_slab_green");
    public static final Identifier PURPLE_ARENA_BLOCK_SLAB_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_slab_purple");
    public static final Identifier RED_ARENA_BLOCK_SLAB_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_slab_red");

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
        if (resourceId.equals(ORANGE_ARENA_BLOCK) || resourceId.equals(ORANGE_ARENA_BLOCK_ITEM)) {
            return new OrangeArenaBlockModel();
        } else if(resourceId.equals(PINK_ARENA_BLOCK) || resourceId.equals(PINK_ARENA_BLOCK_ITEM)){
            return new PinkArenaBlockModel();
        } else if(resourceId.equals(BLUE_ARENA_BLOCK) || resourceId.equals(BLUE_ARENA_BLOCK_ITEM)){
            return new BlueArenaBlockModel();
        } else if(resourceId.equals(YELLOW_ARENA_BLOCK) || resourceId.equals(YELLOW_ARENA_BLOCK_ITEM)){
            return new YellowArenaBlockModel();
        } else if(resourceId.equals(GREEN_ARENA_BLOCK) || resourceId.equals(GREEN_ARENA_BLOCK_ITEM)){
            return new GreenArenaBlockModel();
        } else if(resourceId.equals(PURPLE_ARENA_BLOCK) || resourceId.equals(PURPLE_ARENA_BLOCK_ITEM)){
            return new PurpleArenaBlockModel();
        } else if(resourceId.equals(RED_ARENA_BLOCK) || resourceId.equals(RED_ARENA_BLOCK_ITEM)){
            return new RedArenaBlockModel();

        } else if (resourceId.equals(ORANGE_ARENA_PILLAR_BLOCK) || resourceId.equals(ORANGE_ARENA_PILLAR_BLOCK_ITEM)) {
            return new OrangeArenaPillarBlockModel();
        } else if (resourceId.equals(PINK_ARENA_PILLAR_BLOCK) || resourceId.equals(PINK_ARENA_PILLAR_BLOCK_ITEM)) {
            return new PinkArenaPillarBlockModel();
        } else if (resourceId.equals(BLUE_ARENA_PILLAR_BLOCK) || resourceId.equals(BLUE_ARENA_PILLAR_BLOCK_ITEM)) {
            return new BlueArenaPillarBlockModel();
        } else if (resourceId.equals(YELLOW_ARENA_PILLAR_BLOCK) || resourceId.equals(YELLOW_ARENA_PILLAR_BLOCK_ITEM)) {
            return new YellowArenaPillarBlockModel();
        } else if (resourceId.equals(GREEN_ARENA_PILLAR_BLOCK) || resourceId.equals(GREEN_ARENA_PILLAR_BLOCK_ITEM)) {
            return new GreenArenaPillarBlockModel();
        } else if (resourceId.equals(PURPLE_ARENA_PILLAR_BLOCK) || resourceId.equals(PURPLE_ARENA_PILLAR_BLOCK_ITEM)) {
            return new PurpleArenaPillarBlockModel();
        } else if (resourceId.equals(RED_ARENA_PILLAR_BLOCK) || resourceId.equals(RED_ARENA_PILLAR_BLOCK_ITEM)) {
            return new RedArenaPillarBlockModel();

        } else if (resourceId.equals(ORANGE_ARENA_BLOCK_STAIRS) || resourceId.equals(ORANGE_ARENA_BLOCK_STAIRS_ITEM)) {
            return new OrangeArenaBlockStairsModel();
        } else if (resourceId.equals(PINK_ARENA_BLOCK_STAIRS) || resourceId.equals(PINK_ARENA_BLOCK_STAIRS_ITEM)) {
            return new PinkArenaBlockStairsModel();
        } else if (resourceId.equals(BLUE_ARENA_BLOCK_STAIRS) || resourceId.equals(BLUE_ARENA_BLOCK_STAIRS_ITEM)) {
            return new BlueArenaBlockStairsModel();
        } else if (resourceId.equals(YELLOW_ARENA_BLOCK_STAIRS) || resourceId.equals(YELLOW_ARENA_BLOCK_STAIRS_ITEM)) {
            return new YellowArenaBlockStairsModel();
        } else if (resourceId.equals(GREEN_ARENA_BLOCK_STAIRS) || resourceId.equals(GREEN_ARENA_BLOCK_STAIRS_ITEM)) {
            return new GreenArenaBlockStairsModel();
        } else if (resourceId.equals(PURPLE_ARENA_BLOCK_STAIRS) || resourceId.equals(PURPLE_ARENA_BLOCK_STAIRS_ITEM)) {
            return new PurpleArenaBlockStairsModel();
        } else if (resourceId.equals(RED_ARENA_BLOCK_STAIRS) || resourceId.equals(RED_ARENA_BLOCK_STAIRS_ITEM)) {
            return new RedArenaBlockStairsModel();

        } else if (resourceId.equals(ORANGE_ARENA_DIVIDER) || resourceId.equals(ORANGE_ARENA_DIVIDER_ITEM)) {
            return new OrangeArenaDividerBlockModel();
        } else if (resourceId.equals(PINK_ARENA_DIVIDER) || resourceId.equals(PINK_ARENA_DIVIDER_ITEM)) {
            return new PinkArenaDividerBlockModel();
        } else if (resourceId.equals(BLUE_ARENA_DIVIDER) || resourceId.equals(BLUE_ARENA_DIVIDER_ITEM)) {
            return new BlueArenaDividerBlockModel();
        } else if (resourceId.equals(YELLOW_ARENA_DIVIDER) || resourceId.equals(YELLOW_ARENA_DIVIDER_ITEM)) {
            return new YellowArenaDividerBlockModel();
        } else if (resourceId.equals(GREEN_ARENA_DIVIDER) || resourceId.equals(GREEN_ARENA_DIVIDER_ITEM)) {
            return new GreenArenaDividerBlockModel();
        } else if (resourceId.equals(PURPLE_ARENA_DIVIDER) || resourceId.equals(PURPLE_ARENA_DIVIDER_ITEM)) {
            return new PurpleArenaDividerBlockModel();
        } else if (resourceId.equals(RED_ARENA_DIVIDER) || resourceId.equals(RED_ARENA_DIVIDER_ITEM)) {
            return new RedArenaDividerBlockModel();

        } else if (resourceId.equals(ORANGE_ARENA_BLOCK_SLAB) || resourceId.equals(ORANGE_ARENA_BLOCK_SLAB_ITEM)) {
            return new OrangeArenaBlockSlabModel();
        } else if (resourceId.equals(PINK_ARENA_BLOCK_SLAB) || resourceId.equals(PINK_ARENA_BLOCK_SLAB_ITEM)) {
            return new PinkArenaBlockSlabModel();
        } else if (resourceId.equals(BLUE_ARENA_BLOCK_SLAB) || resourceId.equals(BLUE_ARENA_BLOCK_SLAB_ITEM)) {
            return new BlueArenaBlockSlabModel();
        } else if (resourceId.equals(YELLOW_ARENA_BLOCK_SLAB) || resourceId.equals(YELLOW_ARENA_BLOCK_SLAB_ITEM)) {
            return new YellowArenaBlockSlabModel();
        } else if (resourceId.equals(GREEN_ARENA_BLOCK_SLAB) || resourceId.equals(GREEN_ARENA_BLOCK_SLAB_ITEM)) {
            return new GreenArenaBlockSlabModel();
        } else if (resourceId.equals(PURPLE_ARENA_BLOCK_SLAB) || resourceId.equals(PURPLE_ARENA_BLOCK_SLAB_ITEM)) {
            return new PurpleArenaBlockSlabModel();
        } else if (resourceId.equals(RED_ARENA_BLOCK_SLAB) || resourceId.equals(RED_ARENA_BLOCK_SLAB_ITEM)) {
            return new RedArenaBlockSlabModel();
        }

        return null;
    }
}

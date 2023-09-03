package de.kleiner3.lasertag.block.models;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.block.models.concrete.*;
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
    public static final Identifier ARENA_BLOCK = new Identifier(LasertagMod.ID, "block/arena_block");
    public static final Identifier ARENA_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_block");
    public static final Identifier ARENA_PILLAR_BLOCK = new Identifier(LasertagMod.ID, "block/arena_pillar_block");
    public static final Identifier ARENA_PILLAR_BLOCK_ITEM = new Identifier(LasertagMod.ID, "item/arena_pillar_block");
    public static final Identifier ARENA_BLOCK_STAIRS = new Identifier(LasertagMod.ID, "block/arena_block_stairs");
    public static final Identifier ARENA_BLOCK_STAIRS_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_stairs");
    public static final Identifier ARENA_DIVIDER = new Identifier(LasertagMod.ID, "block/arena_divider");
    public static final Identifier ARENA_DIVIDER_ITEM = new Identifier(LasertagMod.ID, "item/arena_divider");
    public static final Identifier ARENA_BLOCK_SLAB = new Identifier(LasertagMod.ID, "block/arena_block_slab");
    public static final Identifier ARENA_BLOCK_SLAB_ITEM = new Identifier(LasertagMod.ID, "item/arena_block_slab");

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
        if (resourceId.equals(ARENA_BLOCK) || resourceId.equals(ARENA_BLOCK_ITEM)) {
            return new ArenaBlockModel();
        } else if (resourceId.equals(ARENA_PILLAR_BLOCK) || resourceId.equals(ARENA_PILLAR_BLOCK_ITEM)) {
            return new ArenaPillarBlockModel();
        } else if (resourceId.equals(ARENA_BLOCK_STAIRS) || resourceId.equals(ARENA_BLOCK_STAIRS_ITEM)) {
            return new ArenaBlockStairsModel();
        } else if (resourceId.equals(ARENA_DIVIDER) || resourceId.equals(ARENA_DIVIDER_ITEM)) {
            return new ArenaDividerBlockModel();
        } else if (resourceId.equals(ARENA_BLOCK_SLAB) || resourceId.equals(ARENA_BLOCK_SLAB_ITEM)) {
            return new ArenaBlockSlabModel();
        }

        return null;
    }
}

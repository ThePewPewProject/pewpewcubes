package de.kleiner3.lasertag.block.models;

import com.mojang.datafixers.util.Pair;
import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class for all emissive slabs
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public class EmissiveSlabModel extends AbstractEmissiveBlockModel {

    private final SpriteIdentifier sideTextureSpriteId;
    private final SpriteIdentifier glowSideTextureSpriteId;

    protected Sprite sideTextureSprite;
    protected Sprite glowSideTextureSprite;

    public EmissiveSlabModel(String texturePath,
                             String glowTexturePath,
                             String sideTexturePath,
                             String glowSideTexturePath) {
        super(texturePath, glowTexturePath);

        sideTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, sideTexturePath));
        glowSideTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, glowSideTexturePath));
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {

        var textureDependencies = super.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences);

        textureDependencies.add(sideTextureSpriteId);
        textureDependencies.add(glowSideTextureSpriteId);

        return textureDependencies;
    }

    @Override
    protected void getSprites(Function<SpriteIdentifier, Sprite> textureGetter) {

        super.getSprites(textureGetter);

        sideTextureSprite = textureGetter.apply(sideTextureSpriteId);
        glowSideTextureSprite = textureGetter.apply(glowSideTextureSpriteId);
    }

    @Override
    protected void addTextureToUnbakedModel(QuadEmitter emitter, Sprite textureSprite) {
        // Do not implement
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {

        var isUpperSlab = blockState.get(SlabBlock.TYPE) == SlabType.TOP;

        var emitter = renderContext.getEmitter();

        addSlabTexture(emitter, isUpperSlab);

        // Render the mesh
        renderContext.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {

        var emitter = renderContext.getEmitter();

        addSlabTexture(emitter, false);

        // Render the mesh
        renderContext.meshConsumer().accept(mesh);
    }

    private void addSlabTexture(QuadEmitter emitter, boolean isUpperSlab)  {

        if (isUpperSlab) {
            addSlabTextureUp(emitter);
        } else {
            addSlabTextureDown(emitter);
        }
    }

    private void addSlabTextureUp(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {

            switch (direction) {
                case UP -> addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case DOWN -> addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
                case NORTH, EAST, SOUTH, WEST -> addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.5f, 1.0f, 1.0f, 0.0f);
            }
        }
    }

    private void addSlabTextureDown(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {

            switch (direction) {
                case UP -> addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
                case DOWN -> addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case NORTH, EAST, SOUTH, WEST -> addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);
            }
        }
    }
}

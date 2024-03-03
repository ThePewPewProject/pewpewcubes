package de.pewpewproject.lasertag.block.models;

import com.mojang.datafixers.util.Pair;
import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class for all emissive custom block models using the divider 3d model
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public abstract class EmissiveDividerBlockModel extends AbstractEmissiveBlockModel {

    private final SpriteIdentifier sideTextureSpriteId;
    private final SpriteIdentifier glowSideTextureSpriteId;
    private final SpriteIdentifier frontTextureSpriteId;
    private final SpriteIdentifier glowFrontTextureSpriteId;

    protected Sprite sideTextureSprite;
    protected Sprite glowSideTextureSprite;
    protected Sprite frontTextureSprite;
    protected Sprite glowFrontTextureSprite;

    public EmissiveDividerBlockModel(String frontTexturePath,
                                     String glowFrontTexturePath,
                                     String sideTexturePath,
                                     String glowSideTexturePath) {

        sideTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, sideTexturePath));
        glowSideTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, glowSideTexturePath));
        frontTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, frontTexturePath));
        glowFrontTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, glowFrontTexturePath));
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {

        var textureDependencies = new ArrayList<SpriteIdentifier>(4);

        textureDependencies.add(sideTextureSpriteId);
        textureDependencies.add(glowSideTextureSpriteId);
        textureDependencies.add(frontTextureSpriteId);
        textureDependencies.add(glowFrontTextureSpriteId);

        return textureDependencies;
    }

    @Override
    protected void getSprites(Function<SpriteIdentifier, Sprite> textureGetter) {

        sideTextureSprite = textureGetter.apply(sideTextureSpriteId);
        glowSideTextureSprite = textureGetter.apply(glowSideTextureSpriteId);
        frontTextureSprite = textureGetter.apply(frontTextureSpriteId);
        glowFrontTextureSprite = textureGetter.apply(glowFrontTextureSpriteId);
    }

    @Override
    public Sprite getParticleSprite() {
        // Block break particle
        return frontTextureSprite;
    }

    @Override
    protected void addTextureToUnbakedModel(QuadEmitter emitter) {
        // Do not implement
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {

        var facingAxis = blockState.get(HorizontalFacingBlock.FACING).getAxis();

        var emitter = renderContext.getEmitter();

        addDividerTexture(emitter, facingAxis);

        // Render the mesh
        renderContext.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {

        var emitter = renderContext.getEmitter();

        addDividerTexture(emitter, Direction.Axis.X);

        // Render the mesh
        renderContext.meshConsumer().accept(mesh);
    }

    private void addDividerTexture(QuadEmitter emitter, Direction.Axis axis) {
        if (axis.equals(Direction.Axis.X)) {
            addDividerTextureXAxis(emitter);
        } else {
            addDividerTextureZAxis(emitter);
        }
    }

    private void addDividerTextureXAxis(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {
            switch (direction) {
                case UP, DOWN -> addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 7.0f / 16.0f, 1.0f, 9.0f / 16.0f, 0.0f, MutableQuadView.BAKE_ROTATE_90);
                case EAST, WEST -> addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 7.0f / 16.0f, 0.0f, 9.0f / 16.0f, 1.0f, 0.0f);
                case NORTH, SOUTH -> addTexture(direction, frontTextureSprite, glowFrontTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 7.0f / 16.0f);
            }
        }
    }

    private void addDividerTextureZAxis(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {
            switch (direction) {
                case UP, DOWN, NORTH, SOUTH -> addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 7.0f / 16.0f, 0.0f, 9.0f / 16.0f, 1.0f, 0.0f);
                case EAST, WEST -> addTexture(direction, frontTextureSprite, glowFrontTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 7.0f / 16.0f);
            }
        }
    }
}

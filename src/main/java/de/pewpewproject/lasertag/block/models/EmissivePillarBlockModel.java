package de.pewpewproject.lasertag.block.models;

import com.mojang.datafixers.util.Pair;
import de.pewpewproject.lasertag.LasertagMod;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
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
 * Base class for all emissive pillar block models
 *
 * @author Étienne Muser
 */
public abstract class EmissivePillarBlockModel extends AbstractEmissiveBlockModel {

    private final SpriteIdentifier topTextureSpriteId;
    private final SpriteIdentifier glowTopTextureSpriteId;
    private final SpriteIdentifier sideTextureSpriteId;
    private final SpriteIdentifier glowSideTextureSpriteId;

    protected Sprite topTextureSprite;
    protected Sprite glowTopTextureSprite;
    protected Sprite sideTextureSprite;
    protected Sprite glowSideTextureSprite;

    public EmissivePillarBlockModel(String topTexturePath,
                                    String glowTopTexturePath,
                                    String sideTexturePath,
                                    String glowSideTexturePath) {
        topTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, topTexturePath));
        glowTopTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, glowTopTexturePath));
        sideTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, sideTexturePath));
        glowSideTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, glowSideTexturePath));
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {

        var textureDependencies = new ArrayList<SpriteIdentifier>(4);

        textureDependencies.add(sideTextureSpriteId);
        textureDependencies.add(glowSideTextureSpriteId);
        textureDependencies.add(topTextureSpriteId);
        textureDependencies.add(glowTopTextureSpriteId);

        return textureDependencies;
    }

    @Override
    protected void getSprites(Function<SpriteIdentifier, Sprite> textureGetter) {

        sideTextureSprite = textureGetter.apply(sideTextureSpriteId);
        glowSideTextureSprite = textureGetter.apply(glowSideTextureSpriteId);
        topTextureSprite = textureGetter.apply(topTextureSpriteId);
        glowTopTextureSprite = textureGetter.apply(glowTopTextureSpriteId);
    }

    @Override
    public Sprite getParticleSprite() {
        return topTextureSprite;
    }

    @Override
    protected void addTextureToUnbakedModel(QuadEmitter emitter) {
        // Do not implement
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {

        var axis = blockState.get(PillarBlock.AXIS);

        var emitter = renderContext.getEmitter();

        addPillarTexture(emitter, axis);

        // Render the mesh
        renderContext.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {

        var emitter = renderContext.getEmitter();

        addPillarTexture(emitter, Direction.Axis.Y);

        // Render the mesh
        renderContext.meshConsumer().accept(mesh);
    }

    private void addPillarTexture(QuadEmitter emitter, Direction.Axis axis) {

        switch (axis) {
            case X -> addPillarTextureX(emitter);
            case Y -> addPillarTextureY(emitter);
            case Z -> addPillarTextureZ(emitter);
        }
    }

    private void addPillarTextureX(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {
            switch (direction) {
                case EAST, WEST -> addTexture(direction, topTextureSprite, glowTopTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case UP, DOWN, NORTH, SOUTH -> addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, MutableQuadView.BAKE_ROTATE_90);
            }
        }
    }

    private void addPillarTextureY(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {
            switch (direction) {
                case UP, DOWN -> addTexture(direction, topTextureSprite, glowTopTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case NORTH, EAST, SOUTH, WEST -> addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            }
        }
    }

    private void addPillarTextureZ(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {
            switch (direction) {
                case NORTH, SOUTH -> addTexture(direction, topTextureSprite, glowTopTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case UP, DOWN -> addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case EAST, WEST -> addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, MutableQuadView.BAKE_ROTATE_90);
            }
        }
    }
}

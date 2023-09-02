package de.kleiner3.lasertag.block.models;

import com.mojang.datafixers.util.Pair;
import de.kleiner3.lasertag.LasertagMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
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
 * Base class for all emissive stairs
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public abstract class EmissiveStairsModel extends AbstractEmissiveBlockModel {

    private final SpriteIdentifier stairTextureSpriteId;
    private final SpriteIdentifier glowStairTextureSpriteId;
    private final SpriteIdentifier sideTextureSpriteId;
    private final SpriteIdentifier glowSideTextureSpriteId;

    protected Sprite stairTextureSprite;
    protected Sprite glowStairTextureSprite;
    protected Sprite sideTextureSprite;
    protected Sprite glowSideTextureSprite;

    public EmissiveStairsModel(String texturePath,
                               String glowTexturePath,
                               String stairTexturePath,
                               String glowStairTexturePath,
                               String sideTexturePath,
                               String glowSideTexturePath) {
        super(texturePath, glowTexturePath);

        stairTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, stairTexturePath));
        glowStairTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, glowStairTexturePath));
        sideTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, sideTexturePath));
        glowSideTextureSpriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(LasertagMod.ID, glowSideTexturePath));
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {

        var textureDependencies = super.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences);

        textureDependencies.add(stairTextureSpriteId);
        textureDependencies.add(glowStairTextureSpriteId);
        textureDependencies.add(sideTextureSpriteId);
        textureDependencies.add(glowSideTextureSpriteId);

        return textureDependencies;
    }

    @Override
    protected void getSprites(Function<SpriteIdentifier, Sprite> textureGetter) {

        super.getSprites(textureGetter);

        stairTextureSprite = textureGetter.apply(stairTextureSpriteId);
        glowStairTextureSprite = textureGetter.apply(glowStairTextureSpriteId);
        sideTextureSprite = textureGetter.apply(sideTextureSpriteId);
        glowSideTextureSprite = textureGetter.apply(glowSideTextureSpriteId);
    }

    @Override
    protected void addTextureToUnbakedModel(QuadEmitter emitter, Sprite textureSprite) {
        // Do not implement
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {

        var facingDirection = blockState.get(StairsBlock.FACING);

        var emitter = renderContext.getEmitter();

        addStairTexture(emitter, facingDirection);

        // Render the mesh
        renderContext.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {

        var emitter = renderContext.getEmitter();

        addStairTexture(emitter, Direction.SOUTH);

        // Render the mesh
        renderContext.meshConsumer().accept(mesh);
    }

    private void addStairTexture(QuadEmitter emitter, Direction blockFacingDirection) {

        switch (blockFacingDirection) {
            case NORTH -> addStairTextureNorth(emitter);
            case EAST -> addStairTextureEast(emitter);
            case SOUTH -> addStairTextureSouth(emitter);
            case WEST -> addStairTextureWest(emitter);
        }
    }

    private void addStairTextureNorth(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {

            switch (direction) {
                case UP -> {
                    // Upper part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.5f, 1.0f, 1.0f, 0.0f);

                    // Lower part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.5f);
                }
                case DOWN, NORTH -> addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case EAST -> {
                    // Base of the side
                    addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f);

                    // Top of the side
                    addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f);
                }
                case SOUTH -> {
                    // Lower part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);

                    // Upper part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.5f, 1.0f, 1.0f, 0.5f);
                }
                case WEST -> {
                    // Base of the side
                    addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);

                    // Top of the side
                    addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.5f, 0.5f, 1.0f, 0.0f);
                }
            }
        }
    }

    private void addStairTextureEast(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {

            switch (direction) {
                case UP -> {
                    // Upper part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, MutableQuadView.BAKE_ROTATE_90);

                    // Lower part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.0f, 0.5f, 1.0f, 0.5f, MutableQuadView.BAKE_ROTATE_90);
                }
                case DOWN, EAST -> addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case SOUTH -> {
                    // Base of the side
                    addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f);

                    // Top of the side
                    addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f);
                }
                case WEST -> {
                    // Upper part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);

                    // Lower part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.5f, 1.0f, 1.0f, 0.5f);
                }
                case NORTH -> {
                    // Base of the side
                    addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);

                    // Top of the side
                    addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.5f, 0.5f, 1.0f, 0.0f);
                }
            }
        }
    }

    private void addStairTextureSouth(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {

            switch (direction) {
                case UP -> {
                    // Lower part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.5f, 1.0f, 1.0f, 0.5f);

                    // Upper part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);
                }
                case DOWN, SOUTH -> addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case WEST -> {
                    // Top of the side
                    addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f);

                    // Base of the side
                    addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f);
                }
                case NORTH -> {
                    // Lower part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);

                    // Upper part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.5f, 1.0f, 1.0f, 0.5f);
                }
                case EAST -> {
                    // Top of the side
                    addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);

                    // Base of the side
                    addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 0.5f, 1.0f, 0.0f);
                }
            }
        }
    }

    private void addStairTextureWest(QuadEmitter emitter) {
        for(Direction direction : Direction.values()) {

            switch (direction) {
                case UP -> {
                    // Lower part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.5f, 0.0f, 1.0f, 1.0f, 0.5f, MutableQuadView.BAKE_ROTATE_90);

                    // Upper part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.0f, 0.5f, 1.0f, 0.0f, MutableQuadView.BAKE_ROTATE_90);
                }
                case DOWN, WEST -> addTexture(direction, textureSprite, glowTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
                case NORTH -> {
                    // Base of the side
                    addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f, 0.5f, 0.0f, 1.0f);

                    // Top of the side
                    addTextureROI(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f);
                }
                case EAST -> {
                    // Lower part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);

                    // Upper part of the stair
                    addTextureROI(direction, stairTextureSprite, glowStairTextureSprite, emitter, 0.0f, 0.5f, 1.0f, 1.0f, 0.5f);
                }
                case SOUTH -> {
                    // Horizontal part of the stair
                    addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 1.0f, 0.5f, 0.0f);

                    // Vertical part of the stair
                    addTexture(direction, sideTextureSprite, glowSideTextureSprite, emitter, 0.0f, 0.0f, 0.5f, 1.0f, 0.0f);
                }
            }
        }
    }
}

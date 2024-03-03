package de.pewpewproject.lasertag.block.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Abstract base class for all emissive custom block models
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractEmissiveBlockModel implements UnbakedModel, BakedModel, FabricBakedModel {

    private RenderMaterial emissiveMaterial;

    protected Mesh mesh;

    @Override
    public Collection<Identifier> getModelDependencies() {
        // This model does not depend on other models.
        return Collections.emptyList();
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {

        getSprites(textureGetter);

        // Build the mesh using the Renderer API
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();
        MaterialFinder materialFinder = renderer.materialFinder();
        emissiveMaterial = materialFinder.emissive(0, true)
                .disableDiffuse(0, true)
                .disableAo(0, true)
                .blendMode(0, BlendMode.TRANSLUCENT)
                .find();

        // Add texture - implemented by child class
        addTextureToUnbakedModel(emitter);

        mesh = builder.build();

        return this;
    }

    /**
     * Get the textures the model needs.
     *
     * @param textureGetter
     */
    protected abstract void getSprites(Function<SpriteIdentifier, Sprite> textureGetter);

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction face, net.minecraft.util.math.random.Random random) {
        // Don't need because we use FabricBakedModel instead. However, it's better to not return null in case some mod decides to call this function.
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        // we want the block to have a shadow depending on the adjacent blocks
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public boolean isVanillaAdapter() {
        // False to trigger FabricBakedModel rendering
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<net.minecraft.util.math.random.Random> supplier, RenderContext renderContext) {
        // Render function

        // We just render the mesh
        renderContext.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {
        // Item render function

        // We just render the mesh of the block
        renderContext.meshConsumer().accept(mesh);
    }

    /**
     * Adds the desired texture to the unbaked model during baking
     *
     * @param emitter
     */
    protected abstract void addTextureToUnbakedModel(QuadEmitter emitter);

    /**
     * Add a single glowing sprite to a texture.
     * <p>
     * If the texture is not square, it gets stretched to a square.
     * If you don't want this to happen, use <code>addTextureROI</code>.
     *
     * @param direction
     * @param textureSprite
     * @param glowTextureSprite
     * @param emitter
     * @param left
     * @param bottom
     * @param right
     * @param top
     * @param depth
     */
    protected void addTexture(Direction direction,
                              Sprite textureSprite,
                              Sprite glowTextureSprite,
                              QuadEmitter emitter,
                              float left,
                              float bottom,
                              float right,
                              float top,
                              float depth) {

        // Add a new face to the mesh
        emitter.square(direction, left, bottom, right, top, depth);
        // Set the sprite of the face, must be called after .square()
        // We haven't specified any UV coordinates, so we want to use the whole texture. BAKE_LOCK_UV does exactly that.
        emitter.spriteBake(0, textureSprite, MutableQuadView.BAKE_LOCK_UV);
        // Enable texture usage
        emitter.spriteColor(0, -1, -1, -1, -1);
        // Add the quad to the mesh
        emitter.emit();

        // Add a new face to the mesh
        emitter.square(direction, left, bottom, right, top, depth);
        // Set the sprite of the face, must be called after .square()
        // We haven't specified any UV coordinates, so we want to use the whole texture. BAKE_LOCK_UV does exactly that.
        emitter.spriteBake(0, glowTextureSprite, MutableQuadView.BAKE_LOCK_UV);
        // Enable texture usage
        emitter.spriteColor(0, -1, -1, -1, -1);
        // Add glow
        emitter.material(emissiveMaterial);
        // Add the quad to the mesh
        emitter.emit();
    }

    /**
     * Add a single glowing sprite to a texture with the selected UV
     *
     * @param direction
     * @param textureSprite
     * @param glowTextureSprite
     * @param emitter
     * @param left
     * @param bottom
     * @param right
     * @param top
     * @param depth
     */
    protected void addTextureROI(Direction direction,
                                 Sprite textureSprite,
                                 Sprite glowTextureSprite,
                                 QuadEmitter emitter,
                                 float left,
                                 float bottom,
                                 float right,
                                 float top,
                                 float depth,
                                 float textureStartX,
                                 float textureStartY,
                                 float textureEndX,
                                 float textureEndY,
                                 int additionalFlags) {

        // Add a new face to the mesh
        emitter.square(direction, left, bottom, right, top, depth);
        // Select region of interest in the texture
        addTextureROI(emitter, textureStartX, textureStartY, textureEndX, textureEndY);
        // Set the sprite of the face, must be called after .square()
        emitter.spriteBake(0, textureSprite, MutableQuadView.BAKE_NORMALIZED | additionalFlags);
        // Enable texture usage
        emitter.spriteColor(0, -1, -1, -1, -1);
        // Add the quad to the mesh
        emitter.emit();

        // Add a new face to the mesh
        emitter.square(direction, left, bottom, right, top, depth);
        // Select region of interest in the texture
        addTextureROI(emitter, textureStartX, textureStartY, textureEndX, textureEndY);
        // Set the sprite of the face, must be called after .square()
        emitter.spriteBake(0, glowTextureSprite, MutableQuadView.BAKE_NORMALIZED | additionalFlags);
        // Enable texture usage
        emitter.spriteColor(0, -1, -1, -1, -1);
        // Add glow
        emitter.material(emissiveMaterial);
        // Add the quad to the mesh
        emitter.emit();
    }

    /**
     * Add a single glowing sprite to a texture with the selected UV
     *
     * @param direction
     * @param textureSprite
     * @param glowTextureSprite
     * @param emitter
     * @param left
     * @param bottom
     * @param right
     * @param top
     * @param depth
     */
    protected void addTextureROI(Direction direction,
                                 Sprite textureSprite,
                                 Sprite glowTextureSprite,
                                 QuadEmitter emitter,
                                 float left,
                                 float bottom,
                                 float right,
                                 float top,
                                 float depth,
                                 float textureStartX,
                                 float textureStartY,
                                 float textureEndX,
                                 float textureEndY) {
        addTextureROI(direction, textureSprite, glowTextureSprite, emitter,left, bottom, right, top, depth, textureStartX, textureStartY, textureEndX, textureEndY, 0);
    }

    /**
     * Add a single glowing sprite to a texture with the selected UV.
     * Overload where the whole texture is selected.
     *
     * @param direction
     * @param textureSprite
     * @param glowTextureSprite
     * @param emitter
     * @param left
     * @param bottom
     * @param right
     * @param top
     * @param depth
     */
    protected void addTextureROI(Direction direction,
                                 Sprite textureSprite,
                                 Sprite glowTextureSprite,
                                 QuadEmitter emitter,
                                 float left,
                                 float bottom,
                                 float right,
                                 float top,
                                 float depth,
                                 int additionalFlags) {
        addTextureROI(direction, textureSprite, glowTextureSprite, emitter, left, bottom, right, top, depth, 0.0f, 0.0f, 1.0f, 1.0f, additionalFlags);
    }

    /**
     * Add a single glowing sprite to a texture with the selected UV.
     * Overload where the whole texture is selected.
     *
     * @param direction
     * @param textureSprite
     * @param glowTextureSprite
     * @param emitter
     * @param left
     * @param bottom
     * @param right
     * @param top
     * @param depth
     */
    protected void addTextureROI(Direction direction,
                                 Sprite textureSprite,
                                 Sprite glowTextureSprite,
                                 QuadEmitter emitter,
                                 float left,
                                 float bottom,
                                 float right,
                                 float top,
                                 float depth) {
        addTextureROI(direction, textureSprite, glowTextureSprite, emitter, left, bottom, right, top, depth, 0.0f, 0.0f, 1.0f, 1.0f, 0);
    }

    private void addTextureROI(QuadEmitter emitter,
                               float textureStartX,
                               float textureStartY,
                               float textureEndX,
                               float textureEndY) {
        emitter.sprite(0, 0, textureStartX, textureStartY);
        emitter.sprite(1, 0, textureStartX, textureEndY);
        emitter.sprite(2, 0, textureEndX, textureEndY);
        emitter.sprite(3, 0, textureEndX, textureStartY);
    }
}

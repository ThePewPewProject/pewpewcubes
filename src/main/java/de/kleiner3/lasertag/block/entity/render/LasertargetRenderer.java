package de.kleiner3.lasertag.block.entity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import de.kleiner3.lasertag.block.entity.LaserTargetBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import software.bernie.geckolib3.util.EModelRenderCycle;

/**
 * Renderer for the lasertarget
 *
 * @author Étienne Muser
 */
public class LasertargetRenderer extends GeoBlockRenderer<LaserTargetBlockEntity> {

    private static final LasertargetModel MODEL = new LasertargetModel();

    private static final LasertargetLightsModel LIGHTS_MODEL = new LasertargetLightsModel();

    // Period length int game ticks of blinking lights
    private static final double PERIOD = 32.0;

    public LasertargetRenderer(BlockEntityRendererFactory.Context context) {
        super(MODEL);
    }

    @Override
    public void render(LaserTargetBlockEntity blockEntity, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {

        // Render base
        GeoModel model = modelProvider.getModel(modelProvider.getModelResource(blockEntity));
        this.dispatchedMat = poseStack.peek().getPositionMatrix().copy();
        this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
        poseStack.push();

        positionBlock(blockEntity, poseStack);

        RenderSystem.setShaderTexture(0, getTextureLocation(blockEntity));
        Color renderColor = getRenderColor(blockEntity, partialTick, poseStack, bufferSource, null, packedLight);
        RenderLayer renderType = RenderLayer.getEntityCutout(getTextureLocation(animatable));
        render(model, blockEntity, partialTick, renderType, poseStack, bufferSource, null, packedLight, OverlayTexture.DEFAULT_UV,
                renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
                renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
        poseStack.pop();

        // Render lights
        poseStack.push();

        var lightsModel = LIGHTS_MODEL.getModel(LIGHTS_MODEL.getModelResource(null));

        var cameo = RenderLayer.getEntityTranslucentEmissive(LIGHTS_MODEL.getTextureResource(null));

        float brightness = doSineWave(blockEntity.getWorld().getTime());

        var r = 1.0f;
        var g = 0;
        var b = 0;

        if (blockEntity.isDeactivated()) {
            brightness = 0.0f;
        }

        positionBlock(blockEntity, poseStack);
        this.render(lightsModel, blockEntity, partialTick, cameo, poseStack, bufferSource, bufferSource.getBuffer(cameo), 255, OverlayTexture.DEFAULT_UV, r, g, b, brightness);
        poseStack.pop();
    }

    private void positionBlock(LaserTargetBlockEntity blockEntity, MatrixStack poseStack) {
        BlockState blockState = blockEntity.getCachedState();
        var face = blockState.get(WallMountedBlock.FACE);
        var facing = blockState.get(WallMountedBlock.FACING);

        switch (face) {
            case FLOOR -> {
                poseStack.translate(0, 0.01f, 0);
                poseStack.translate(0.5, 0, 0.5);
            }
            case WALL -> {
                switch (facing) {
                    case SOUTH -> {
                        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
                        poseStack.translate(0.5, 0.0, -0.5);
                    }
                    case WEST -> {
                        poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
                        poseStack.translate(0.5, -1.0, 0.5);
                    }
                    case NORTH -> {
                        poseStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(270));
                        poseStack.translate(0.5, -1.0, 0.5);
                    }
                    case EAST -> {
                        poseStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(270));
                        poseStack.translate(-0.5, 0.0, 0.5);
                    }
                }
            }
            case CEILING -> {
                poseStack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(180));
                poseStack.translate(-0.5, -1.0, 0.5);
            }
        }
    }

    private float doSineWave(long x) {
        return ((float)Math.cos((x * Math.PI * 2.0) / PERIOD) + 1.0f) * 0.5f;
    }
}

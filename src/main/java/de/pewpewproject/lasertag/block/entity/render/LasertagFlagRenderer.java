package de.pewpewproject.lasertag.block.entity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import de.pewpewproject.lasertag.block.entity.LasertagFlagBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;
import software.bernie.geckolib3.util.EModelRenderCycle;

/**
 * Custom renderer for the lasertag flag block entity
 *
 * @author Étienne Muser
 */
public class LasertagFlagRenderer extends GeoBlockRenderer<LasertagFlagBlockEntity> {

    private static final LasertagFlagModel MODEL = new LasertagFlagModel();

    private static final LasertagFlagLightsModel LIGHTS_MODEL = new LasertagFlagLightsModel();

    public LasertagFlagRenderer(BlockEntityRendererFactory.Context context) {
        super(MODEL);
    }

    @Override
    public void render(LasertagFlagBlockEntity blockEntity, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {

        // Only render if is bottom half
        if (blockEntity.getHalf() == DoubleBlockHalf.UPPER) {
            return;
        }

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var syncedState = gameManager.getSyncedState();
        var teamsConfigState = syncedState.getTeamsConfigState();

        // Render base
        GeoModel model = modelProvider.getModel(modelProvider.getModelResource(blockEntity));
        this.dispatchedMat = poseStack.peek().getPositionMatrix().copy();
        this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
        poseStack.push();

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

        // Get the team of the flag
        var teamOptional = teamsConfigState.getTeamOfName(blockEntity.getTeamName());

        var r = 1.0f;
        var g = 1.0f;
        var b = 1.0f;
        var brightness = 1.0f;

        // If flag has team
        if (teamOptional.isPresent()) {

            var color = teamOptional.get().color();
            r = color.r() / 255.0f;
            g = color.g() / 255.0f;
            b = color.b() / 255.0f;
        }

        this.render(lightsModel, blockEntity, partialTick, cameo, poseStack, bufferSource, bufferSource.getBuffer(cameo), 255, OverlayTexture.DEFAULT_UV, r, g, b, brightness);
        poseStack.pop();
    }
}

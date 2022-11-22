package de.kleiner3.lasertag.entity.render.armor;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.item.Items;
import de.kleiner3.lasertag.item.LasertagVestItem;
import de.kleiner3.lasertag.lasertaggame.PlayerDeactivatedManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeoUtils;

/**
 * Renderer for the lasertag vest
 *
 * @author Étienne Muser
 */
public class LasertagVestRenderer extends GeoArmorRenderer<LasertagVestItem> {

    private static final LasertagVestModel VEST_MODEL = new LasertagVestModel();
    private static final LasertagVestLightsModel LIGHTS_MODEL = new LasertagVestLightsModel();

    public LasertagVestRenderer() {
        super(VEST_MODEL);

        this.headBone = "armorHead";
        this.bodyBone = "armorBody";
        this.rightArmBone = "armorRightArm";
        this.leftArmBone = "armorLeftArm";
        this.rightLegBone = "armorRightLeg";
        this.leftLegBone = "armorLeftLeg";
        this.rightBootBone = "armorRightBoot";
        this.leftBootBone = "armorLeftBoot";
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity,
                       EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
        super.render(matrices, vertexConsumers, stack, entity, slot, light, contextModel);

        RenderLayer cameo = RenderLayer.getEyes(LIGHTS_MODEL.getTextureResource(null));
        matrices.push();

        matrices.translate(0.0D, 1.497F, 0.0D);
        matrices.scale(-1.005F, -1.0F, 1.005F);

        var lightsModel = LIGHTS_MODEL.getModel(LIGHTS_MODEL.getModelResource(null));

        if (this.bodyBone != null) {
            IBone bodyBone = LIGHTS_MODEL.getBone(this.bodyBone);
            GeoUtils.copyRotations(contextModel.body, bodyBone);
            bodyBone.setPositionX(contextModel.body.pivotX);
            bodyBone.setPositionY(-contextModel.body.pivotY);
            bodyBone.setPositionZ(contextModel.body.pivotZ);
        }

        // Default color is black
        float r = 0;
        float g = 0;
        float b = 0;

        // If player is activated
        if (entity instanceof PlayerEntity) {
            boolean isDeactivated = PlayerDeactivatedManager.isDeactivated(entity.getUuid());

            if (isDeactivated == false) {
                var color = ((LasertagVestItem) Items.LASERTAG_VEST).getColor(stack);
                r = ((color >> 16) & 0xFF) / 255.0F;
                g = ((color >> 8) & 0xFF) / 255.0F;
                b = ((color >> 0) & 0xFF) / 255.0F;
            }
        }

        this.render(lightsModel, (LasertagVestItem) Items.LASERTAG_VEST, 1.0F, cameo, matrices, vertexConsumers, vertexConsumers.getBuffer(cameo), light, OverlayTexture.DEFAULT_UV, r, g, b, 1.0F);
        matrices.pop();
    }

    @Override
    public Color getRenderColor(LasertagVestItem animatable, float partialTicks, MatrixStack stack, @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn) {
        if (getGeoModelProvider() == LIGHTS_MODEL) {
            var color = animatable.getColor(itemStack);
            return Color.ofOpaque(color);
        }
        return super.getRenderColor(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn);
    }
}

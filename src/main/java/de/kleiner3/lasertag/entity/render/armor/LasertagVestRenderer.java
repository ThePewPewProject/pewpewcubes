package de.kleiner3.lasertag.entity.render.armor;

import de.kleiner3.lasertag.item.LasertagVestItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

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
        setModel(VEST_MODEL);
        super.render(matrices, vertexConsumers, stack, entity, slot, light, contextModel);
        setModel(LIGHTS_MODEL);
        super.render(matrices, vertexConsumers, stack, entity, slot, light, contextModel);
    }

    @Override
    public Color getRenderColor(LasertagVestItem animatable, float partialTicks, MatrixStack stack, @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn) {
        if (modelProvider == LIGHTS_MODEL) {
            var color = animatable.getColor(itemStack);
            return Color.ofOpaque(color);
        }
        return super.getRenderColor(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn);
    }
}

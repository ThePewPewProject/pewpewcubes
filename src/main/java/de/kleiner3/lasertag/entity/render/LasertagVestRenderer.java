package de.kleiner3.lasertag.entity.render;

import de.kleiner3.lasertag.LasertagMod;
import de.kleiner3.lasertag.entity.render.model.LasertagVestModel;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class LasertagVestRenderer implements ArmorRenderer {
    private static LasertagVestRenderer renderer = null;

    private LasertagVestRenderer() {
        super();
    }

    public static LasertagVestRenderer getInstance() {
        if (renderer == null) {
            renderer = new LasertagVestRenderer();
        }

        return renderer;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
        var modelData = LasertagVestModel.getTexturedModelData();
        LasertagVestModel model = new LasertagVestModel(modelData.createModel());
        VertexConsumer vertexConsumer = VEST_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);//ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getEntitySolid(getTexture()), false, stack.hasGlint());//RenderLayer.getArmorCutoutNoCull(getTexture()), false, stack.hasGlint());
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    }

    public static Identifier getTexture() {
        return new Identifier(LasertagMod.ID, "textures/armor/lasertag_vest.png");
    }
}

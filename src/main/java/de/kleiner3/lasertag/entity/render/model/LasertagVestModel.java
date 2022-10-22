package de.kleiner3.lasertag.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

// Made with Blockbench 4.4.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class LasertagVestModel extends EntityModel<Entity> {
    private final ModelPart Vest;
    private ModelPart cube_r1;
    private ModelPart cube_r2;
    private ModelPart cube_r3;
    private ModelPart cube_r4;
    private ModelPart cube_r5;
    private ModelPart cube_r6;
    public LasertagVestModel(ModelPart root) {
        this.Vest = root.getChild("Vest");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData Vest = modelPartData.addChild("Vest", ModelPartBuilder.create().uv(9, 9).cuboid(0.0F, 2.0F, 0.4F, 6.0F, 4.0F, 0.0F, new Dilation(0.0F))
                .uv(1, 12).mirrored().cuboid(-0.8F, -4.25F, 0.8F, 1.0F, 0.0F, 4.0F, new Dilation(0.0F)).mirrored(false)
                .uv(8, 22).mirrored().cuboid(-0.8F, -4.0F, 0.825F, 1.0F, 0.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(5, 12).cuboid(-0.8F, -4.0F, 5.0F, 1.0F, 0.0F, 0.0F, new Dilation(0.0F))
                .uv(3, 9).cuboid(-1.475F, 5.575F, 0.775F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(2, 13).cuboid(5.0F, -4.25F, 0.825F, 1.0F, 0.0F, 4.0F, new Dilation(0.0F))
                .uv(9, 23).cuboid(5.0F, -4.0F, 0.825F, 1.0F, 0.0F, 0.0F, new Dilation(0.0F))
                .uv(5, 15).cuboid(5.0F, -4.0F, 5.0F, 1.0F, 0.0F, 0.0F, new Dilation(0.0F))
                .uv(3, 15).cuboid(6.875F, 5.575F, 0.775F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 0).mirrored().cuboid(0.5F, -2.5F, 0.625F, 5.0F, 4.0F, 0.0F, new Dilation(0.0F)).mirrored(false)
                .uv(15, 17).cuboid(1.0F, 0.4F, 5.0F, 4.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 4.0F, -3.0F));

        ModelPartData cube_r1 = Vest.addChild("cube_r1", ModelPartBuilder.create().uv(7, 18).cuboid(-1.0F, -3.0F, -0.175F, 2.0F, 3.0F, 0.0F, new Dilation(0.0F))
                .uv(10, 17).cuboid(-1.0F, -2.0F, -4.525F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.25F, 6.0F, 5.175F, 0.0F, 0.0F, -0.7854F));

        ModelPartData cube_r2 = Vest.addChild("cube_r2", ModelPartBuilder.create().uv(2, 24).cuboid(-0.75F, -0.5F, -0.175F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(20, 21).cuboid(-0.75F, -0.5F, -4.45F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(6.25F, 6.0F, 5.175F, 0.0F, 0.0F, -1.1781F));

        ModelPartData cube_r3 = Vest.addChild("cube_r3", ModelPartBuilder.create().uv(4, 14).cuboid(-0.95F, -1.5F, -0.1F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F))
                .uv(9, 19).cuboid(-0.95F, -1.5F, -4.325F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(5.3071F, -2.287F, 5.1F, 0.0F, 0.0F, 0.3927F));

        ModelPartData cube_r4 = Vest.addChild("cube_r4", ModelPartBuilder.create().uv(6, 13).cuboid(-1.0F, -1.5F, -0.1F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F))
                .uv(17, 15).cuboid(-1.0F, -1.5F, -4.325F, 1.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.6929F, -2.287F, 5.1F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r5 = Vest.addChild("cube_r5", ModelPartBuilder.create().uv(5, 18).cuboid(-1.0F, -3.0F, -0.175F, 2.0F, 3.0F, 0.0F, new Dilation(0.0F))
                .uv(7, 14).cuboid(-1.0F, -2.0F, -4.525F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 6.0F, 5.175F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r6 = Vest.addChild("cube_r6", ModelPartBuilder.create().uv(4, 20).cuboid(-0.75F, -0.5F, -0.175F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(4, 16).cuboid(-0.75F, -0.5F, -4.45F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 6.0F, 5.175F, 0.0F, 0.0F, 1.1781F));
        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        Vest.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
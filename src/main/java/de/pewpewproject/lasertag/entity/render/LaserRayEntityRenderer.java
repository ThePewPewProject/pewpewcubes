package de.pewpewproject.lasertag.entity.render;

import de.pewpewproject.lasertag.entity.LaserRayEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

/**
 * A custom renderer to render the laser ray entity as a smaller beacon beam
 *
 * @author Étienne Muser
 */
public class LaserRayEntityRenderer extends EntityRenderer<LaserRayEntity> {
    public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

    public LaserRayEntityRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(LaserRayEntity var1) {
        return BEAM_TEXTURE;
    }

    /**
     * Renders the entity
     */
    @Override
    public void render(LaserRayEntity laserRayEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        long worldTime = laserRayEntity.getWorld().getTime();
        int color = laserRayEntity.getColor();
        float[] colorArr = new float[] {
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                ((color) & 0xFF) / 255.0F
        };
        Vec3d end = laserRayEntity.getEnd().subtract(laserRayEntity.getStart());
        renderBeam(matrices, vertexConsumers, tickDelta, worldTime, colorArr, end);
    }

    /**
     * Render a beacon beam
     *
     * @param matrices The matrix stack
     * @param vertexConsumers The vertex consumer provider
     * @param color the color float array
     * @param end The end position of the ray relative to its start position
     */
    private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, long worldTime, float[] color, Vec3d end) {
        renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, worldTime, end, color, 0.0125f, 0.015625f);
    }

    /**
     * Render a beacon beam
     *
     * @param matrices The matrix stack
     * @param vertexConsumers The vertex consumer provider
     */
    public static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickDelta, long worldTime, Vec3d end, float[] color, float innerRadius, float outerRadius) {
        double length = end.length();
        Quaternion rot = getRot(end);
        matrices.push();

        float beamSpinProgress = (float) Math.floorMod(worldTime, 40) + tickDelta;
        float h = MathHelper.fractionalPart(beamSpinProgress * 0.2f - (float) MathHelper.floor(beamSpinProgress * 0.1f));
        float colorR = color[0];
        float colorG = color[1];
        float colorB = color[2];
        matrices.push();
        matrices.multiply(rot);
        float m;
        float n = innerRadius;
        float o = innerRadius;
        float p;
        float q = -innerRadius;
        float r;
        float s;
        float t = -innerRadius;
        float w = -1.0f + h;
        float x = (float) length * (0.5f / innerRadius) + w;
        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)), colorR, colorG, colorB, 1.0f, length, 0.0f, n, o, 0.0f, q, 0.0f, 0.0f, t, x, w);
        matrices.pop();
        matrices.multiply(rot);
        m = -outerRadius;
        n = -outerRadius;
        o = outerRadius;
        p = -outerRadius;
        q = -outerRadius;
        r = outerRadius;
        s = outerRadius;
        t = outerRadius;
        w = -1.0f + h;
        x = (float) length + w;
        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), colorR, colorG, colorB, 0.125f, length, m, n, o, p, q, r, s, t, x, w);
        matrices.pop();
    }

    /**
     * Renders a single layer of a beacon
     */
    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, double length, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float v1, float v2) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, length, x1, z1, x2, z2, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, length, x4, z4, x3, z3, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, length, x2, z2, x4, z4, v1, v2);
        renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, length, x3, z3, x1, z1, v1, v2);
    }

    private static void renderBeamFace(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, double length, float x1, float z1, float x2, float z2, float v1, float v2) {
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, length, x1, z1, (float) 1.0, v1);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, 0, x1, z1, (float) 1.0, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, 0, x2, z2, (float) 0.0, v2);
        renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, length, x2, z2, (float) 0.0, v1);
    }

    /**
     * @param v the top-most coordinate of the texture region
     * @param u the left-most coordinate of the texture region
     */
    private static void renderBeamVertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, double y, float x, float z, float u, float v) {
        vertices.vertex(positionMatrix, x, (float) y, z).color(red, green, blue, alpha).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    /**
     * Helper method to get the rotation quaternion to rotate the up-vector to the direction vector
     *
     * @param direction The direction vector
     * @return The rotation quaternion
     */
    private static Quaternion getRot(Vec3d direction) {
        Vec3d up = new Vec3d(0, 1, 0);

        Vec3d cross = up.crossProduct(direction);
        double dot = up.dotProduct(direction);

        Quaternion q;
        if (direction.y < 0 && Math.abs(direction.x) < 0.001F && Math.abs(direction.z) < 0.001F) {
            q = new Quaternion(1.0F, 0.0F, 0.0F, (float) Math.cos(0.5 * Math.PI));
        } else {
            q = new Quaternion((float) cross.x, (float) cross.y, (float) cross.z,
                    (float) direction.length() + (float) dot);
        }

        q.normalize();
        return q;
    }
}

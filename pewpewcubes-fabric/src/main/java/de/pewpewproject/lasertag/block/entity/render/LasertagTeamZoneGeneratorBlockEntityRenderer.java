package de.pewpewproject.lasertag.block.entity.render;

import de.pewpewproject.lasertag.LasertagMod;
import de.pewpewproject.lasertag.block.entity.LasertagTeamZoneGeneratorBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * @author Étienne Muser
 */
public class LasertagTeamZoneGeneratorBlockEntityRenderer implements BlockEntityRenderer<LasertagTeamZoneGeneratorBlockEntity> {

    // Period length int game ticks of blinking border when team was not found
    private static final double PERIOD = 32.0;
    public static final Identifier WHITE_TEXTURE_ID = new Identifier(LasertagMod.ID, "textures/block/white.png");

    public LasertagTeamZoneGeneratorBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(LasertagTeamZoneGeneratorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int r;
        int g;
        int b;

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var syncedState = gameManager.getSyncedState();
        var teamsConfigManager = syncedState.getTeamsConfigState();

        // Get the team
        var teamDtoOptional = teamsConfigManager.getTeamOfName(entity.getTeamName());

        if (teamDtoOptional.isPresent()){
            var colorDto = teamDtoOptional.get().color();
            r = colorDto.r();
            g = colorDto.g();
            b = colorDto.b();
        } else {
            r = (int)(255 * doSineWave(entity.getWorld().getTime()));
            g = 0;
            b = 0;
        }

        // Get the render layer
        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(WHITE_TEXTURE_ID));

        // Get the border cache
        var borders = entity.getBorderCache();

        // Draw every face in the borders
        borders.forEach(border -> {
            drawFace(vertexConsumer, matrices, entity, border.x(), border.y(), r, g, b, 255);
        });
    }

    /**
     * Draws a block face to the lasertag team zone generator block entity.
     *
     * @param vertexConsumer The vertex consumer to use. Use vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(WHITE_TEXTURE_ID, true))!
     * @param matrices The matrix stack
     * @param entity The lasertag team zone generator block entity
     * @param destination The block pos of the team zone block to render the face of. Position in world coordinates.
     * @param direction The direction of the face to render
     * @param r The red pixel component
     * @param g The green pixel component
     * @param b The blue pixel component
     * @param a The alpha pixel component
     */
    private static void drawFace(VertexConsumer vertexConsumer, MatrixStack matrices, LasertagTeamZoneGeneratorBlockEntity entity, BlockPos destination, Direction direction, int r, int g, int b, int a) {

        matrices.push();

        var offset = destination.subtract(entity.getPos());

        matrices.translate(offset.getX(), offset.getY(), offset.getZ());
        matrices.multiply(direction.getRotationQuaternion());

        var positionMatrix = matrices.peek().getPositionMatrix();
        var normalMatrix = matrices.peek().getNormalMatrix();

        switch (direction) {
            case NORTH -> {
                vertexConsumer.vertex(positionMatrix, 0, 0, -1).color(r, g, b, a).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, -1, 0, -1).color(r, g, b, a).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, -1, 0, 0).color(r, g, b, a).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 0, 0, 0).color(r, g, b, a).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
            }
            case EAST -> {
                vertexConsumer.vertex(positionMatrix, 0, 1, -1).color(r, g, b, a).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, -1, 1, -1).color(r, g, b, a).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, -1, 1, 0).color(r, g, b, a).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 0, 1, 0).color(r, g, b, a).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
            }
            case SOUTH -> {
                vertexConsumer.vertex(positionMatrix, 0, 1, 0).color(r, g, b, a).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 1, 1, 0).color(r, g, b, a).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 1, 1, -1).color(r, g, b, a).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 0, 1, -1).color(r, g, b, a).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
            }
            case WEST -> {
                vertexConsumer.vertex(positionMatrix, 0, 0, 0).color(r, g, b, a).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 1, 0, 0).color(r, g, b, a).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 1, 0, -1).color(r, g, b, a).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 0, 0, -1).color(r, g, b, a).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
            }
            case UP -> {
                vertexConsumer.vertex(positionMatrix, 0, 1, 1).color(r, g, b, a).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 1, 1, 1).color(r, g, b, a).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 1, 1, 0).color(r, g, b, a).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 0, 1, 0).color(r, g, b, a).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
            }
            case DOWN -> {
                vertexConsumer.vertex(positionMatrix, 0, 0, 0).color(r, g, b, a).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 1, 0, 0).color(r, g, b, a).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 1, 0, -1).color(r, g, b, a).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
                vertexConsumer.vertex(positionMatrix, 0, 0, -1).color(r, g, b, a).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0, 1, 0).next();
            }
            default -> {
                LasertagMod.LOGGER.error("Unrecognized direction '{}'", direction);
            }
        }


        matrices.pop();
    }

    private static float doSineWave(long x) {
        return ((float)Math.cos((x * Math.PI * 2.0) / PERIOD) + 1.0f) * 0.5f;
    }

    @Override
    public boolean rendersOutsideBoundingBox(LasertagTeamZoneGeneratorBlockEntity blockEntity) {
        return true;
    }
}

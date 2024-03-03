package de.pewpewproject.lasertag.block.entity.render;

import de.pewpewproject.lasertag.block.entity.LasertagStartGameButtonBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

/**
 * Custom renderer for the lasertag start game button block entity to render the "Start Game" label.
 *
 * @author Étienne Muser
 */
@Environment(EnvType.CLIENT)
public class LasertagStartGameButtonBlockEntityRenderer implements BlockEntityRenderer<LasertagStartGameButtonBlockEntity> {

    private EntityRenderDispatcher dispatcher;
    private TextRenderer textRenderer;

    public LasertagStartGameButtonBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.dispatcher = ctx.getEntityRenderDispatcher();
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(LasertagStartGameButtonBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var uiState = gameManager.getSyncedState().getUIState();

        // If a game is running, do not render the name tags
        if (uiState.isGameRunning) {
            return;
        }

        var blockPos = entity.getPos();
        double d = this.dispatcher.getSquaredDistanceToCamera(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (d <= 4096.0) {
            var text = Text.translatable("render.start_game_button_text");

            double yOffset;
            switch (entity.getCachedState().get(AbstractButtonBlock.FACE)) {
                case FLOOR -> yOffset = 0.5;
                case WALL -> yOffset = 1.0;
                case CEILING -> yOffset = 0.5;
                default -> yOffset = 0.0;
            }

            matrices.push();
            matrices.translate(0.5, yOffset, 0.5);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int j = (int)(g * 255.0F) << 24;
            TextRenderer textRenderer = this.textRenderer;
            float h = (float)(-textRenderer.getWidth(text) / 2);
            textRenderer.draw(text, h, 0.0f, 553648127, false, matrix4f, vertexConsumers, true, j, light);
            textRenderer.draw(text, h, 0.0f, -1, false, matrix4f, vertexConsumers, false, 0, light);

            matrices.pop();
        }
    }
}

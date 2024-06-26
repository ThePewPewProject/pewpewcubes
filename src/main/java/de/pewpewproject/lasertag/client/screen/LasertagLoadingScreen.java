package de.pewpewproject.lasertag.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.pewpewproject.lasertag.common.util.ThreadUtil;
import de.pewpewproject.lasertag.lasertaggame.state.synced.implementation.UIState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to implement the custom lasertag loading screen
 *
 * @author Étienne Muser
 */
public class LasertagLoadingScreen extends Screen {

    private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");

    private final ScheduledExecutorService spinnerThread;

    private int spinnerProgress = 0;

    public LasertagLoadingScreen() {
        super(NarratorManager.EMPTY);

        spinnerThread = ThreadUtil.createScheduledExecutor("client-arena-load-spinner-thread");
        spinnerThread.scheduleAtFixedRate(() ->  {
            spinnerProgress++;
            spinnerProgress %= 5;
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void stopSpinner() {
        ThreadUtil.attemptShutdown(spinnerThread, 2000);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        // Get the game managers
        var world = MinecraftClient.getInstance().world;
        var gameManager = world.getClientLasertagManager();
        var uiState = gameManager.getSyncedState().getUIState();

        var hMid = this.height / 2.0F;
        var wMid = this.width / 2.0F;

        this.renderBackground();
        this.renderVignette();

        var textColor = 0xFFFFFFFF;
        var stepString = uiState.mapLoadingStepString + "...";
        var textWidth = this.textRenderer.getWidth(stepString);
        this.textRenderer.drawWithShadow(matrices, Text.literal(stepString), wMid - (textWidth / 2.0F), hMid - 15, textColor);

        int barStart = (int)(wMid - (UIState.progressBarWidth / 2.0));
        int progressWidth = (int) (UIState.progressBarWidth * uiState.mapLoadingProgress);
        int barUpperHeight = (int)hMid + 10;
        int barLowerHeight = (int)hMid + 15;

        // Draw background
        DrawableHelper.fill(matrices, barStart, barUpperHeight, barStart + UIState.progressBarWidth, barLowerHeight, UIState.boxColor);

        // Draw progress
        DrawableHelper.fill(matrices, barStart, barUpperHeight, barStart + progressWidth, barLowerHeight, 0xFFFFFFFF);

        // Get the spinner text
        var spinnerText = switch (spinnerProgress) {
            case 0 -> "o . . . .";
            case 1 -> ". o . . .";
            case 2 -> ". . o . .";
            case 3 -> ". . . o .";
            case 4 -> ". . . . o";
            default -> ". . . . .";
        };

        // Draw spinner
        var spinnerTextWidth = this.textRenderer.getWidth(spinnerText);
        textRenderer.draw(matrices, Text.literal(spinnerText), wMid - (spinnerTextWidth / 2.0F), hMid + 20, textColor);

        super.render(matrices, mouseX, mouseY, delta);
    }

    // Override the keyPressed so that the player can't close the loading screen with escape
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    private void renderBackground() {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        var a = 0.5f;
        bufferBuilder.vertex(0.0,     this.height, this.getZOffset()).texture(0.0f,                          0.0f                          ).color(a, a, a, 1.0f).next();
        bufferBuilder.vertex(this.width, this.height, this.getZOffset()).texture(this.width * 0.015625f * 2.0f, 0.0f                          ).color(a, a, a, 1.0f).next();
        bufferBuilder.vertex(this.width, 0.0,      this.getZOffset()).texture(this.width * 0.015625f * 2.0f, this.height * 0.015625f * 2.0f).color(a, a, a, 1.0f).next();
        bufferBuilder.vertex(0.0,     0.0,      this.getZOffset()).texture(0.0f,                          this.height * 0.015625f * 2.0f).color(a, a, a, 1.0f).next();
        tessellator.draw();
    }

    private void renderVignette() {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0,     this.height, this.getZOffset()).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(this.width, this.height, this.getZOffset()).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(this.width, 0.0,      this.getZOffset()).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(0.0,     0.0,      this.getZOffset()).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        tessellator.draw();
        RenderSystem.disableBlend();
    }
}

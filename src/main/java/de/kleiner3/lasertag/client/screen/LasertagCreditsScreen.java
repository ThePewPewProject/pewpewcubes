package de.kleiner3.lasertag.client.screen;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.kleiner3.lasertag.LasertagMod;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * The lasertag credits screen - Heavily inspired by the standard Minecraft credits screen
 *
 * @author Étienne Muser
 */
public class LasertagCreditsScreen extends Screen {
    private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
    private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
    private static final Text SEPARATOR_LINE = Text.literal("============").formatted(Formatting.WHITE);
    private static final String CENTERED_LINE_PREFIX = "           ";
    private static final String OBFUSCATION_PLACEHOLDER = "" + Formatting.WHITE + Formatting.OBFUSCATED + Formatting.GREEN + Formatting.AQUA;
    private float time;
    private List<OrderedText> credits;
    private IntSet centeredLines;
    private int creditsHeight;
    private boolean spaceKeyPressed;
    private final IntSet pressedCtrlKeys = new IntOpenHashSet();
    private float speed;
    private final float baseSpeed;

    public LasertagCreditsScreen() {
        super(NarratorManager.EMPTY);
        this.baseSpeed = 0.5f;
        this.speed = this.baseSpeed;
    }

    private float getSpeed() {
        if (this.spaceKeyPressed) {
            return this.baseSpeed * (5.0f + (float)this.pressedCtrlKeys.size() * 15.0f);
        }
        return this.baseSpeed;
    }

    @Override
    public void tick() {
        this.client.getMusicTracker().tick();
        this.client.getSoundManager().tick(false);
        float f = this.creditsHeight + this.height + this.height + 24;
        if (this.time > f) {
            this.closeScreen();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            this.pressedCtrlKeys.add(keyCode);
        } else if (keyCode == GLFW.GLFW_KEY_SPACE) {
            this.spaceKeyPressed = true;
        }
        this.speed = this.getSpeed();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_SPACE) {
            this.spaceKeyPressed = false;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            this.pressedCtrlKeys.remove(keyCode);
        }
        this.speed = this.getSpeed();
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        this.closeScreen();
    }

    private void closeScreen() {
        this.client.setScreen(null);
    }

    @Override
    protected void init() {
        if (this.credits != null) {
            return;
        }
        this.credits = Lists.newArrayList();
        this.centeredLines = new IntOpenHashSet();
        this.load("texts/credits.json", this::readCredits);
        this.load("texts/postcredits.txt", this::readPoem);
        this.creditsHeight = this.credits.size() * 12;
    }

    private void load(String id, LasertagCreditsScreen.CreditsReader reader) {
        try (BufferedReader reader2 = this.client.getResourceManager().openAsReader(new Identifier(LasertagMod.ID, id))){
            reader.read(reader2);
        }
        catch (Exception exception) {
            LasertagMod.LOGGER.error("Couldn't load lasertag credits", exception);
        }
    }

    private void readPoem(Reader reader) throws IOException {
        int i;
        String line;
        BufferedReader bufferedReader = new BufferedReader(reader);
        Random random = Random.create(8124371L);
        while ((line = bufferedReader.readLine()) != null) {
            line = line.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
            while ((i = line.indexOf(OBFUSCATION_PLACEHOLDER)) != -1) {
                String string2 = line.substring(0, i);
                String string3 = line.substring(i + OBFUSCATION_PLACEHOLDER.length());
                line = string2 + Formatting.WHITE + Formatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + string3;
            }
            this.addText(line);
            this.addEmptyLine();
        }
        for (i = 0; i < 8; ++i) {
            this.addEmptyLine();
        }
    }

    private void readCredits(Reader reader) {
        JsonArray jsonArray = JsonHelper.deserializeArray(reader);
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String string = jsonObject.get("section").getAsString();
            this.addText(SEPARATOR_LINE, true);
            this.addText(Text.literal(string).formatted(Formatting.YELLOW), true);
            this.addText(SEPARATOR_LINE, true);
            this.addEmptyLine();
            this.addEmptyLine();
            JsonArray jsonArray2 = jsonObject.getAsJsonArray("titles");
            for (JsonElement jsonElement2 : jsonArray2) {
                JsonObject jsonObject2 = jsonElement2.getAsJsonObject();
                String string2 = jsonObject2.get("title").getAsString();
                JsonArray jsonArray3 = jsonObject2.getAsJsonArray("names");
                this.addText(Text.literal(string2).formatted(Formatting.GRAY), false);
                for (JsonElement jsonElement3 : jsonArray3) {
                    String string3 = jsonElement3.getAsString();
                    this.addText(Text.literal(CENTERED_LINE_PREFIX).append(string3).formatted(Formatting.WHITE), false);
                }
                this.addEmptyLine();
                this.addEmptyLine();
            }
        }
    }

    private void addEmptyLine() {
        this.credits.add(OrderedText.EMPTY);
    }

    private void addText(String text) {
        this.credits.addAll(this.client.textRenderer.wrapLines(Text.literal(text), 274));
    }

    private void addText(Text text, boolean centered) {
        if (centered) {
            this.centeredLines.add(this.credits.size());
        }
        this.credits.add(text.asOrderedText());
    }

    private void renderBackground() {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        int i = this.width;
        float f = -this.time * 0.5f;
        float g = (float)this.height - 0.5f * this.time;
        float j = this.time / this.baseSpeed;
        float k = j * 0.02f;
        float l = (float)(this.creditsHeight + this.height + this.height + 24) / this.baseSpeed;
        float m = (l - 20.0f - j) * 0.005f;
        if (m < k) {
            k = m;
        }
        if (k > 1.0f) {
            k = 1.0f;
        }
        k *= k;
        k = k * 96.0f / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, this.height, this.getZOffset()).texture(0.0f, f * 0.015625f).color(k, k, k, 1.0f).next();
        bufferBuilder.vertex(i, this.height, this.getZOffset()).texture((float)i * 0.015625f, f * 0.015625f).color(k, k, k, 1.0f).next();
        bufferBuilder.vertex(i, 0.0, this.getZOffset()).texture((float)i * 0.015625f, g * 0.015625f).color(k, k, k, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.getZOffset()).texture(0.0f, g * 0.015625f).color(k, k, k, 1.0f).next();
        tessellator.draw();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int l;
        this.time += delta * this.speed;
        this.renderBackground();
        int i = this.width / 2 - 137;
        int j = this.height + 50;
        float f = -this.time;
        matrices.push();
        matrices.translate(0.0, f, 0.0);
        RenderSystem.setShaderTexture(0, MINECRAFT_TITLE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        this.drawWithOutline(i, j, (x, y) -> {
            this.drawTexture(matrices, x, y, 0, 0, 155, 44);
            this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
        });
        RenderSystem.disableBlend();
        int k = j + 100;
        for (l = 0; l < this.credits.size(); ++l) {
            float g;
            if (l == this.credits.size() - 1 && (g = (float)k + f - (float)(this.height / 2 - 6)) < 0.0f) {
                matrices.translate(0.0, -g, 0.0);
            }
            if ((float)k + f + 12.0f + 8.0f > 0.0f && (float)k + f < (float)this.height) {
                OrderedText orderedText = this.credits.get(l);
                if (this.centeredLines.contains(l)) {
                    this.textRenderer.drawWithShadow(matrices, orderedText, (float)(i + (274 - this.textRenderer.getWidth(orderedText)) / 2), (float)k, 0xFFFFFF);
                } else {
                    this.textRenderer.drawWithShadow(matrices, orderedText, (float)i, (float)k, 0xFFFFFF);
                }
            }
            k += 12;
        }
        matrices.pop();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, VIGNETTE_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR);
        l = this.width;
        int m = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, m, this.getZOffset()).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(l, m, this.getZOffset()).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(l, 0.0, this.getZOffset()).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        bufferBuilder.vertex(0.0, 0.0, this.getZOffset()).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).next();
        tessellator.draw();
        RenderSystem.disableBlend();
        super.render(matrices, mouseX, mouseY, delta);
    }

    @FunctionalInterface
    @Environment(value= EnvType.CLIENT)
    interface CreditsReader {
        void read(Reader var1) throws IOException;
    }
}

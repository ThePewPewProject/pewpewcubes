package de.pewpewproject.lasertag.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.pewpewproject.lasertag.LasertagMod;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * The arena information screen of the lasertag game manager
 *
 * @author Étienne Muser
 */
public class LasertagArenaInfoScreen extends GameManagerScreen {

    private static final int IMAGE_TOP_PADDING = 5;

    private static final int TEXT_IMAGE_PADDING = 5;

    private static final float IMAGE_RATIO = 440.0F / 55.0F;

    private static final int PARAGRAPH_PADDING = 4;

    private final String arenaInfoTextTranslatable;

    private final Identifier arenaImageIdentifier;

    private Text arenaInfoText;

    public LasertagArenaInfoScreen(Screen parent, String arenaTranslatableName, PlayerEntity player) {
        super(parent, "gui.game_manager.arena_info_screen_title." + arenaTranslatableName, player);

        var arenaTypeFiltered = arenaTranslatableName.split("\\.", 2)[1];
        arenaInfoTextTranslatable = "gui.game_manager.arena_info_screen." + arenaTypeFiltered + "_description";
        arenaImageIdentifier = new Identifier(LasertagMod.ID, "textures/gui/arenas/" + arenaTypeFiltered + ".png");

        backgroundColor = 0xB0000000;
    }

    @Override
    protected void init() {
        super.init();

        arenaInfoText = Text.translatable(arenaInfoTextTranslatable);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        matrices.push();

        var availableWidth = client.currentScreen.width - 2 * horizontalPadding;
        var imageHeight = (int)(availableWidth / IMAGE_RATIO);
        var effectiveStartHeight = verticalPadding + textRenderer.fontHeight + IMAGE_TOP_PADDING;

        RenderSystem.setShaderTexture(0, arenaImageIdentifier);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        DrawableHelper.drawTexture(matrices,
                horizontalPadding,
                effectiveStartHeight,
                0,
                0,
                availableWidth,
                imageHeight,
                availableWidth,
                imageHeight);
        RenderSystem.disableBlend();

        float lineYPos = effectiveStartHeight + imageHeight + TEXT_IMAGE_PADDING;
        for (var line : textRenderer.wrapLines(arenaInfoText, availableWidth)) {

            // If the line is an empty line (that means a paragraph is ending)
            if (textRenderer.getWidth(line) <= 0) {
                lineYPos += PARAGRAPH_PADDING;
                continue;
            }

            // Draw the line
            textRenderer.draw(matrices,
                    line,
                    horizontalPadding,
                    lineYPos,
                    0xFFFFFF);

            // Increment line y pos
            lineYPos += (textRenderer.fontHeight + 1.0F);
        }

        matrices.pop();
    }
}

package de.kleiner3.lasertag.client.screen.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * Widget for a simple label
 *
 * @author Étienne Muser
 */
public class LabelWidget extends DrawableHelper implements Drawable, Element, Selectable {

    private final int x;
    private final int y;
    private final TextRenderer textRenderer;
    private final Text text;
    private final int color;

    /**
     * Create a white label
     *
     * @param x The x-position of the label (reference is left)
     * @param y The y-position of the label (reference is top)
     * @param textRenderer The text renderer to use to render the label
     * @param text The text of the widget
     */
    public LabelWidget(int x, int y, TextRenderer textRenderer, Text text) {
        this(x, y, textRenderer, text, 0xFFFFFFFF);
    }

    /**
     * Create a label
     *
     * @param x The x-position of the label (reference is left)
     * @param y The y-position of the label (reference is top)
     * @param textRenderer The text renderer to use to render the label
     * @param text The text of the widget
     * @param color The color of the widget as 0xAARRGGBB
     */
    public LabelWidget(int x, int y, TextRenderer textRenderer, Text text, int color) {
        this.x = x;
        this.y = y;
        this.textRenderer = textRenderer;
        this.text = text;
        this.color = color;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.textRenderer.drawWithShadow(matrices, text, x, y, color);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}

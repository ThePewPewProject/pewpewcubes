package de.kleiner3.lasertag.client.screen;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

/**
 * Base class for screens in the lasertag game manager gui
 *
 * @author Étienne Muser
 */
public abstract class GameManagerScreen extends Screen {

    private int numberOfListedButtons = 0;

    private final Screen parent;
    protected final PlayerEntity player;

    protected static final int horizontalPadding = 20;
    protected static final int verticalPadding = 15;
    protected static final int buttonPadding = 10;
    protected static final int buttonHeight = 20;
    protected static final int buttonWidth = 200;
    protected static final int textColor = 0xFFFFFFFF;

    public GameManagerScreen(Screen parent, String translatableTitle, PlayerEntity player) {
        super(Text.translatable(translatableTitle));

        this.parent = parent;
        this.player = player;
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        this.drawBackground(matrices);
        this.textRenderer.drawWithShadow(matrices, title, horizontalPadding, verticalPadding, textColor);
        DrawableHelper.fill(matrices, horizontalPadding, verticalPadding + this.textRenderer.fontHeight + 1, this.width - horizontalPadding, verticalPadding + this.textRenderer.fontHeight + 2, 0xFFFFFFFF);

        super.render(matrices, mouseX, mouseY, delta);
    }

    /**
     * The init method of the screen. Override to add more gui elements. Must call super.init() first!
     */
    @Override
    protected void init() {
        numberOfListedButtons = 0;

        addBackButton();
    }

    /**
     * Adds a button to the main button list
     *
     * @param translatableName The button text as the translatable key
     * @param pressAction The button click callback
     * @return The created button widget
     */
    protected ButtonWidget addListedButton(String translatableName, ButtonWidget.PressAction pressAction) {

        var button = this.addDrawableChild(new ButtonWidget(horizontalPadding, verticalPadding + textRenderer.fontHeight + buttonPadding + numberOfListedButtons * (buttonHeight + buttonPadding), buttonWidth, buttonHeight, Text.translatable(translatableName), pressAction));

        ++numberOfListedButtons;

        return button;
    }

    private void addBackButton() {

        var buttonTranslatableText = this.parent == null ? "gui.exit" : "gui.back";

        this.addDrawableChild(new ButtonWidget(this.width - horizontalPadding - (buttonWidth / 2), this.height - verticalPadding - buttonHeight, buttonWidth / 2, buttonHeight, Text.translatable(buttonTranslatableText), button -> {
            this.close();
        }));
    }

    private void drawBackground(MatrixStack matrices) {
        DrawableHelper.fill(matrices, 0, 0, this.width, this.height, 0x80000000);
    }
}

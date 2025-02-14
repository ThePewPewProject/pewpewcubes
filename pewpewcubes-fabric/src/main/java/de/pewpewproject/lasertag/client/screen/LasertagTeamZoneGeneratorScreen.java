package de.pewpewproject.lasertag.client.screen;

import de.pewpewproject.lasertag.block.entity.LasertagTeamZoneGeneratorBlockEntity;
import de.pewpewproject.lasertag.client.screen.widget.LabelWidget;
import de.pewpewproject.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

/**
 * @author Étienne Muser
 */
public class LasertagTeamZoneGeneratorScreen extends Screen {

    private static final Text TITLE = Text.translatable("gui.team_zone_generator.title");
    private static final int VERTICAL_PADDING = 15;
    private static final int BUTTON_HEIGHT = 20;
    private static final int EXIT_BUTTON_WIDTH = 100;
    private static final int HORIZONTAL_PADDING = 20;
    private static final int BUTTON_PADDING = 10;
    private static final int BUTTON_WIDTH = 200;

    private final LasertagTeamZoneGeneratorBlockEntity teamZoneGenerator;

    private TextFieldWidget textFieldWidget;

    public LasertagTeamZoneGeneratorScreen(LasertagTeamZoneGeneratorBlockEntity teamZoneGenerator) {
        super(TITLE);

        this.teamZoneGenerator = teamZoneGenerator;
    }

    @Override
    protected void init() {
        super.init();

        // Calculate start pos
        var startX = HORIZONTAL_PADDING;
        var startY = VERTICAL_PADDING + textRenderer.fontHeight + BUTTON_PADDING;

        // Create the team name label
        var labelStartY = startY + (this.textRenderer.fontHeight / 2);
        var labelText = Text.translatable("gui.team_zone_generator.team_name");
        var labelTextWidth = this.textRenderer.getWidth(labelText);
        this.addDrawableChild(new LabelWidget(startX, labelStartY, this.textRenderer, labelText));

        // Create the text input for the team name
        this.textFieldWidget = this.addDrawableChild(new TextFieldWidget(this.textRenderer, startX + labelTextWidth + BUTTON_PADDING, startY, BUTTON_WIDTH, BUTTON_HEIGHT, Text.literal(this.teamZoneGenerator.getTeamName())));
        this.textFieldWidget.setText(this.teamZoneGenerator.getTeamName());
        this.focusOn(this.textFieldWidget);
        this.textFieldWidget.setTextFieldFocused(true);

        // Add the "generate" button
        this.addDrawableChild(new ButtonWidget(startX + labelTextWidth + BUTTON_PADDING + BUTTON_WIDTH + BUTTON_PADDING, startY, BUTTON_WIDTH / 2, BUTTON_HEIGHT, Text.translatable("gui.team_zone_generator.generate"), button -> {
            this.triggerGenerateTeamZone(this.textFieldWidget.getText());
        }));

        // Add warning label
        this.addDrawableChild(new LabelWidget(startX, startY + BUTTON_HEIGHT + BUTTON_PADDING, this.textRenderer, Text.translatable("gui.team_zone_generator.warning_label_text").formatted(Formatting.RED)));
        this.addDrawableChild(new LabelWidget(startX, startY + BUTTON_HEIGHT + BUTTON_PADDING + this.textRenderer.fontHeight + 2, this.textRenderer, Text.translatable("gui.team_zone_generator.change_info_text").formatted(Formatting.RED)));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            this.triggerGenerateTeamZone(this.textFieldWidget.getText());
            this.close();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.drawBackground(matrices);
        DrawableHelper.drawCenteredText(matrices, this.textRenderer, TITLE.getString(), this.width / 2, VERTICAL_PADDING + (this.textRenderer.fontHeight / 2), 0xFFFFFFFF);
        drawExitButton();

        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawBackground(MatrixStack matrices) {
        DrawableHelper.fill(matrices, 0, 0, this.width, this.height, 0x80000000);
    }

    private void drawExitButton() {
        this.addDrawableChild(new ButtonWidget(this.width - HORIZONTAL_PADDING - EXIT_BUTTON_WIDTH, this.height - VERTICAL_PADDING - BUTTON_HEIGHT, EXIT_BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable("gui.exit"), button -> {
            this.close();
        }));
    }

    private void triggerGenerateTeamZone(String teamName) {

        // Create buffer
        var buf = new PacketByteBuf(Unpooled.buffer());

        // Get the block pos
        var pos = this.teamZoneGenerator.getPos();

        // Write the pos
        buf.writeBlockPos(pos);

        // Write the team name
        buf.writeString(teamName);

        ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_GENERATE_ZONE, buf);
    }
}

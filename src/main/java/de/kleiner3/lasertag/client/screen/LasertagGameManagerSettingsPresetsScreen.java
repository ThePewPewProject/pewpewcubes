package de.kleiner3.lasertag.client.screen;

import de.kleiner3.lasertag.client.screen.widget.*;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * The lasertag settings presets screen of the lasertag game manager
 *
 * @author Étienne Muser
 */
public class LasertagGameManagerSettingsPresetsScreen extends GameManagerScreen {

    private ListWidget<String, String> list;
    private TextFieldWidget presetNameTextFieldWidget;

    public LasertagGameManagerSettingsPresetsScreen(Screen parent, PlayerEntity player) {
        super(parent, "gui.game_manager.settings.presets_screen_title", player);
    }

    /**
     * Reload the lists data source
     */
    public void resetList() {
        this.list.refreshDataSource();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // Forward the mouse scrolled event to the list
        return this.list.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Forward the mouse dragged event to the list
        return this.list.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void tick() {
        // Forward the tick event to the preset name text field widget
        this.presetNameTextFieldWidget.tick();
    }

    @Override
    protected void init() {
        super.init();
        this.addAdditionalButtons();

        var columns = new ArrayList<ListColumn<String, String>>(2);

        columns.add(new ListColumn<>(this::getPresetNameColumn, s -> s, 2));
        columns.add(new ListColumn<>(this::getLoadColumn, s -> s, 1));
        columns.add(new ListColumn<>(this::getDeleteColumn, s -> s, 1));

        var columnsDefinition = new ListColumnsDefinition<>(columns);

        var availableHeight = this.height - (2 * verticalPadding + this.textRenderer.fontHeight + 2 * buttonPadding + buttonHeight);

        this.list = this.addDrawableChild(ListWidget.fromAvailableHeight(horizontalPadding, verticalPadding + textRenderer.fontHeight + buttonPadding,
                this.width - 2 * horizontalPadding, availableHeight,
                this::getPresetNames,
                columnsDefinition, this, this.textRenderer));
    }

    /**
     * Add the preset name text field and save button widgets
     */
    private void addAdditionalButtons() {

        this.presetNameTextFieldWidget = this.addDrawableChild(new TextFieldWidget(this.textRenderer, horizontalPadding + 1, this.height - verticalPadding - buttonHeight + 1, buttonWidth - 1, buttonHeight - 2, Text.translatable("gui.game_manager.settings.presets.name")));
        this.presetNameTextFieldWidget.setText("Unnamed_preset");

        this.addDrawableChild(new ButtonWidget(horizontalPadding + buttonWidth + 4, this.height - verticalPadding - buttonHeight, buttonWidth / 2, buttonHeight, Text.translatable("gui.game_manager.settings.presets.save"), button -> {
            // Create packet buffer
            var buf = new PacketByteBuf(Unpooled.buffer());

            // Get the preset name
            var presetName = this.presetNameTextFieldWidget.getText();

            buf.writeString(presetName);

            ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_SAVE_PRESET, buf);

            this.presetNameTextFieldWidget.setText("Unnamed_preset");
        }));
    }

    /**
     * Get the template for the preset name column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getPresetNameColumn(ListCell<String> desc) {
        var startY = desc.y() + (desc.height() / 2) - (this.textRenderer.fontHeight / 2);
        return new LabelWidget(desc.x() + 5, startY, this.textRenderer, Text.literal(desc.value()));
    }

    /**
     * Get the template for the load button column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getLoadColumn(ListCell<String> desc) {
        return new ButtonWidget(desc.x() + 1, desc.y() + 1, desc.width() - 2, desc.height() - 2, Text.translatable("gui.load"), button -> {
            // Create packet buffer
            var buf = new PacketByteBuf(Unpooled.buffer());

            buf.writeString(desc.value());

            ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_LOAD_PRESET, buf);
        });
    }

    /**
     * Get the template for the delete button column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getDeleteColumn(ListCell<String> desc) {

        return new ButtonWidget(desc.x() + 1, desc.y() + 1, desc.width() - 2, desc.height() - 2, Text.translatable("gui.delete"), button -> {
            // Create packet buffer
            var buf = new PacketByteBuf(Unpooled.buffer());

            buf.writeString(desc.value());

            ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_DELETE_PRESET, buf);
        });
    }

    /**
     * The data source for the list
     *
     * @return The preset names
     */
    private List<String> getPresetNames() {
        return new ArrayList<>(LasertagGameManager.getInstance().getPresetsNameManager().getSettingsPresetNames());
    }
}

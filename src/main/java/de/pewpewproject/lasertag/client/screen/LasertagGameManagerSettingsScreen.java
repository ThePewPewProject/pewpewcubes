package de.pewpewproject.lasertag.client.screen;

import de.pewpewproject.lasertag.client.screen.widget.CyclingValueButtonWidget;
import de.pewpewproject.lasertag.client.screen.widget.LabelWidget;
import de.pewpewproject.lasertag.client.screen.widget.YesNoButtonWidget;
import de.pewpewproject.lasertag.client.screen.widget.list.ListCell;
import de.pewpewproject.lasertag.client.screen.widget.list.ListColumn;
import de.pewpewproject.lasertag.client.screen.widget.list.ListColumnsDefinition;
import de.pewpewproject.lasertag.client.screen.widget.list.grouped.GroupedListWidget;
import de.pewpewproject.lasertag.common.types.Tuple;
import de.pewpewproject.lasertag.lasertaggame.settings.SettingDescription;
import de.pewpewproject.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;

import static de.pewpewproject.lasertag.lasertaggame.settings.SettingDataType.BOOL;
import static de.pewpewproject.lasertag.lasertaggame.settings.SettingDataType.LONG;

/**
 * The settings screen of the lasertag game manager
 *
 * @author Étienne Muser
 */
public class LasertagGameManagerSettingsScreen extends GameManagerScreen {

    private GroupedListWidget<SettingRowType> list;

    public LasertagGameManagerSettingsScreen(Screen parent, PlayerEntity player) {
        super(parent, "gui.game_manager.settings_screen_title", player);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Forward the mouse clicked event to the list
        return this.list.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        // Forward the tick to the list - the list contains text field widgets
        this.list.tick();
    }

    @Override
    protected void init() {
        super.init();
        this.addAdditionalButtons();

        var columns = new ArrayList<ListColumn<SettingRowType, UUID>>(3);

        columns.add(new ListColumn<>(this::getNameCellTempate, null, 9));
        columns.add(new ListColumn<>(this::getValueCellTemplate, null, 7));
        columns.add(new ListColumn<>(this::getResetCellTemplate, null, 2));

        var columnsDefinition = new ListColumnsDefinition<>(columns);

        var availableHeight = this.height - (2 * verticalPadding + this.textRenderer.fontHeight + 2 * buttonPadding + buttonHeight);

        this.list = this.addDrawableChild(GroupedListWidget.fromAvailableHeight(horizontalPadding,
                verticalPadding + textRenderer.fontHeight + buttonPadding,
                this.width - 2 * horizontalPadding,
                availableHeight,
                this::getSettingDescriptions,
                columnsDefinition,
                SettingRowType::group,
                this::getGroupingHeaderCellTemplate,
                srt -> srt.x().name(),
                this,
                this.textRenderer));
    }

    private Drawable getGroupingHeaderCellTemplate(String groupingHeader, ListCell<?> cell) {

        var labelText = Text.translatable(groupingHeader).setStyle(Style.EMPTY.withBold(true));

        var startY = cell.y() + (cell.height() / 2) - (this.textRenderer.fontHeight / 2);
        var startX = horizontalPadding + ((this.width - 2 * horizontalPadding) / 2) - (this.textRenderer.getWidth(labelText) / 2);
        return new LabelWidget(startX, startY, this.textRenderer, labelText);
    }

    /**
     * Get the template for the name column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getNameCellTempate(ListCell<SettingRowType> desc) {

        var startY = desc.y() + (desc.height() / 2) - (this.textRenderer.fontHeight / 2);
        return new LabelWidget(desc.x() + 5, startY, this.textRenderer, Text.translatable("gui.game_manager.settings." + desc.value().x().getName()));
    }

    /**
     * Get the template for the value column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getValueCellTemplate(ListCell<SettingRowType> desc) {

        var dataType = desc.value().x().getDataType();
        if (dataType.equals(BOOL)) {
            return this.getBooleanSettingInput(desc.x() + 1, desc.y() + 1, desc.width() - 1, desc.height() - 2, desc.value().x().toString(), (Boolean) desc.value().y());
        } else if (dataType.equals(LONG)) {
            return this.getLongSettingInput(desc.x() + 2, desc.y() + 2, desc.width() - 4, desc.height() - 4, desc.value().x().toString(), (long) desc.value().y());
        } else if (dataType.isEnum()) {
            return this.getEnumSettingInput(desc.x() + 1, desc.y() + 1, desc.width() - 1, desc.height() - 2, desc.value().x().toString(), (Enum<?>)desc.value().y());
        }

        // Default empty drawable
        return (matrices, mouseX, mouseY, delta) -> {};
    }

    /**
     * Get the template for the reset button column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getResetCellTemplate(ListCell<SettingRowType> desc) {

        // If player has not enough rights to edit settings
        if (!this.player.hasPermissionLevel(4)) {
            // Return empty drawable
            return (matrices, mouseX, mouseY, delta) -> {};
        }

        var buttonWidget = new ButtonWidget(desc.x() + 1, desc.y() + 1, desc.width() - 2, desc.height() - 2, Text.translatable("gui.reset"), button -> {
            // Create packet buffer
            var buf = new PacketByteBuf(Unpooled.buffer());

            buf.writeString(desc.value().x().getName());

            ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_SETTING_RESET, buf);
        });
        buttonWidget.active = this.player.hasPermissionLevel(4);

        return buttonWidget;
    }

    /**
     * The data source for the list
     *
     * @return The setting descriptions and values
     */
    private List<SettingRowType> getSettingDescriptions() {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var gameModeManager = gameManager.getGameModeManager();
        var settingsManager = gameManager.getSettingsManager();

        // Get the game mode
        var gameMode = gameModeManager.getGameMode();

        // Get the overwritten settings
        var overwrittenSettings = gameMode.getOverwrittenSettings().stream().map(Tuple::x).collect(Collectors.toSet());

        return gameMode.getRelevantSettings().stream()
                .sorted(Comparator.comparing(SettingDescription::getName))
                .map(s -> {

                    Object value;
                    if (s.getDataType().isEnum()) {
                        value = settingsManager.getEnum(s);
                    } else {
                        value = settingsManager.get(s);
                    }

                    return new SettingRowType(s, value, overwrittenSettings.contains(s) ? "gui.game_manager.settings.group_header.overwritten" : "gui.game_manager.settings.group_header.other");
                }).toList();
    }

    /**
     * Get the template for a boolean setting value
     *
     * @param x The start x-value of the cell
     * @param y The start y-value of the cell
     * @param width The width of the cell
     * @param height The height of the cell
     * @param settingEnumName The enum name of the setting description
     * @param initialValue The initial value of the setting
     * @return The cell template
     */
    private Drawable getBooleanSettingInput(int x, int y, int width, int height, String settingEnumName, boolean initialValue) {
        if (player.hasPermissionLevel(4)) {
            var buttonWidet = new YesNoButtonWidget(x, y, width, height, initialValue, (newValue) -> {
                // Create packet buffer
                var buf = new PacketByteBuf(Unpooled.buffer());

                buf.writeString(settingEnumName);
                buf.writeBoolean(newValue);

                ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_SETTING_CHANGE, buf);
            });

            buttonWidet.active = this.player.hasPermissionLevel(4);

            return buttonWidet;
        } else {
            var startY = y + (height / 2) - (this.textRenderer.fontHeight / 2);
            return new LabelWidget(x + 5, startY, this.textRenderer, Text.translatable(initialValue ? "gui.yes" : "gui.no"));
        }
    }

    /**
     * Get the template for a long setting value
     *
     * @param x The start x-value of the cell
     * @param y The start y-value of the cell
     * @param width The width of the cell
     * @param height The height of the cell
     * @param settingEnumName The enum name of the setting description
     * @param initialValue The initial value of the setting
     * @return The cell template
     */
    private Drawable getLongSettingInput(int x, int y, int width, int height, String settingEnumName, long initialValue) {
        if (player.hasPermissionLevel(4)) {
            var textFieldWidget = new TextFieldWidget(this.textRenderer, x, y, width, height, Text.empty());
            textFieldWidget.setText(Long.toString(initialValue));

            textFieldWidget.setChangedListener(newValue -> {

                // Try to parse new value to long
                try {
                    var newLongValue = Long.parseLong(newValue);

                    if (newLongValue == initialValue) {
                        return;
                    }

                    // Get the setting description
                    var settingDescription = SettingDescription.valueOf(settingEnumName);

                    var minValue = settingDescription.getMinValue();
                    if (minValue != null && newLongValue < (long) minValue) {
                        newLongValue = (long) minValue;
                    }

                    var maxValue = settingDescription.getMaxValue();
                    if (maxValue != null && newLongValue > (long) maxValue) {
                        newLongValue = (long) maxValue;
                    }

                    // Create packet buffer
                    var buf = new PacketByteBuf(Unpooled.buffer());

                    buf.writeString(settingEnumName);
                    buf.writeLong(newLongValue);

                    ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_SETTING_CHANGE, buf);
                } catch (NumberFormatException ignored) {
                    // Do nothing
                }
            });

            return textFieldWidget;
        } else {
            var startY = y + (height / 2) - (this.textRenderer.fontHeight / 2);
            return new LabelWidget(x + 5, startY, this.textRenderer, Text.literal(Long.toString(initialValue)));
        }
    }

    private Drawable getEnumSettingInput(int x, int y, int width, int height, String settingEnumName, Enum<?> initialValue) {
        if (player.hasPermissionLevel(4)) {

            // Get the setting description
            var settingDescription = SettingDescription.valueOf(settingEnumName);

            // Get the enum values
            var enumValues = settingDescription.getDataType().getValueType().getEnumConstants();

            return new CyclingValueButtonWidget<>(x, y, width, height, initialValue,
                    value -> Text.translatable(settingEnumName + "." + ((Enum<?>)value).name()),
                    Arrays.stream(enumValues).toList(),
                    value -> {
                        // Get the enum value as a string
                        var enumValueString = ((Enum<?>)value).name();

                        // Create packet buffer
                        var buf = new PacketByteBuf(Unpooled.buffer());

                        buf.writeString(settingEnumName);
                        buf.writeString(enumValueString);

                        ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_SETTING_CHANGE, buf);
                    });
        } else {
            var startY = y + (height / 2) - (this.textRenderer.fontHeight / 2);
            return new LabelWidget(x + 5, startY, this.textRenderer, Text.translatable(settingEnumName + "." + initialValue.name()));
        }
    }

    /**
     * Add the settings presets and reset all settings buttons
     */
    private void addAdditionalButtons() {

        if (!this.player.hasPermissionLevel(4)) {
            return;
        }

        this.addDrawableChild(new ButtonWidget(horizontalPadding, this.height - verticalPadding - buttonHeight, buttonWidth / 2, buttonHeight, Text.translatable("gui.game_manager.settings.presets"), button -> {
            this.client.setScreen(new LasertagGameManagerSettingsPresetsScreen(this, this.player));
        }));

        this.addDrawableChild(new ButtonWidget(horizontalPadding + (buttonWidth / 2) + 4, this.height - verticalPadding - buttonHeight, buttonWidth / 2, buttonHeight, Text.translatable("gui.game_manager.settings.reset_settings"), button -> {
            ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_SETTINGS_RESET, PacketByteBufs.empty());
        }));
    }

    private static record SettingRowType(SettingDescription x, Object y, String group) {}
}

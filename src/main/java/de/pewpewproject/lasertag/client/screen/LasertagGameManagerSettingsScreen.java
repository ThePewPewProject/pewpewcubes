package de.pewpewproject.lasertag.client.screen;

import de.pewpewproject.lasertag.client.screen.widget.CyclingValueButtonWidget;
import de.pewpewproject.lasertag.client.screen.widget.LabelWidget;
import de.pewpewproject.lasertag.client.screen.widget.YesNoButtonWidget;
import de.pewpewproject.lasertag.client.screen.widget.list.ListCell;
import de.pewpewproject.lasertag.client.screen.widget.list.ListColumn;
import de.pewpewproject.lasertag.client.screen.widget.list.ListColumnsDefinition;
import de.pewpewproject.lasertag.client.screen.widget.list.grouped.GroupedListWidget;
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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.*;

import static de.pewpewproject.lasertag.lasertaggame.settings.SettingDataType.BOOL;
import static de.pewpewproject.lasertag.lasertaggame.settings.SettingDataType.LONG;

/**
 * The settings screen of the lasertag game manager
 *
 * @author Étienne Muser
 */
public class LasertagGameManagerSettingsScreen extends GameManagerScreen {

    /**
     * The height of the search text field widget
     */
    private static final int SEARCH_TEXT_FIELD_HEIGHT = 11;

    /**
     * The width of the search text field widget
     */
    private static final int SEARCH_TEXT_FIELD_WIDTH = 150;

    /**
     * The padding between the search text widget and the settings list
     */
    private static final int SEARCH_TEXT_LIST_PADDING = 5;

    /**
     * The padding between the search text widget and the label in front of it
     */
    private static final int SEARCH_TEXT_LABEL_PADDING = 4;

    private GroupedListWidget<SettingRowType> list;

    private TextFieldWidget searchTextField;

    private String previousSearchText;

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

        var searchTextFieldStartY = verticalPadding + textRenderer.fontHeight + buttonPadding;
        var searchTextFieldLabelStartY = (int)(searchTextFieldStartY + (SEARCH_TEXT_FIELD_HEIGHT / 2.0) - (textRenderer.fontHeight / 2.0));
        var searchTextFieldLabelText = Text.translatable("gui.search").append(":");

        this.addDrawableChild(new LabelWidget(horizontalPadding,
                searchTextFieldLabelStartY,
                textRenderer,
                searchTextFieldLabelText));

        searchTextField = this.addDrawableChild(new TextFieldWidget(textRenderer,
                horizontalPadding + textRenderer.getWidth(searchTextFieldLabelText) + SEARCH_TEXT_LABEL_PADDING,
                searchTextFieldStartY,
                SEARCH_TEXT_FIELD_WIDTH,
                SEARCH_TEXT_FIELD_HEIGHT,
                Text.empty()));

        var columns = new ArrayList<ListColumn<SettingRowType, UUID>>(3);

        columns.add(new ListColumn<>(this::getNameCellTempate, null, 9));
        columns.add(new ListColumn<>(this::getValueCellTemplate, null, 7));
        columns.add(new ListColumn<>(this::getResetCellTemplate, null, 2));

        var columnsDefinition = new ListColumnsDefinition<>(columns);

        var listStartY = searchTextFieldStartY + SEARCH_TEXT_FIELD_HEIGHT + SEARCH_TEXT_LIST_PADDING;
        var availableHeight = this.height - (listStartY + verticalPadding + buttonPadding + buttonHeight);

        this.list = this.addDrawableChild(GroupedListWidget.fromAvailableHeight(horizontalPadding,
                listStartY,
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

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var gameModeManager = gameManager.getGameModeManager();
        var gameMode = gameModeManager.getGameMode();

        var gameModeName = Text.translatable(gameMode.getTranslatableName());
        var labelText = Text.translatable(groupingHeader, gameModeName);
        var labelTextComplete = Text.literal(" ========== ")
                .append(labelText)
                .append(" ========== ")
                .setStyle(Style.EMPTY.withBold(true));

        var startY = cell.y() + (cell.height() / 2) - (this.textRenderer.fontHeight / 2);
        var startX = horizontalPadding + ((this.width - 2 * horizontalPadding) / 2) - (this.textRenderer.getWidth(labelTextComplete) / 2);
        return new LabelWidget(startX, startY, this.textRenderer, labelTextComplete);
    }

    /**
     * Get the template for the name column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getNameCellTempate(ListCell<SettingRowType> desc) {

        var startY = desc.y() + (desc.height() / 2) - (this.textRenderer.fontHeight / 2);
        return new LabelWidget(desc.x() + 5,
                startY,
                this.textRenderer,
                Text.translatable("gui.game_manager.settings." + desc.value().x().getName()),
                Text.translatable("gui.game_manager.settings." + desc.value().x().getName() + ".description"));
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
            return this.getBooleanSettingInput(desc.x() + 1, desc.y() + 1, desc.width() - 1, desc.height() - 2, desc.value().x(), (Boolean) desc.value().y());
        } else if (dataType.equals(LONG)) {
            return this.getLongSettingInput(desc.x() + 2, desc.y() + 2, desc.width() - 4, desc.height() - 4, desc.value().x(), (long) desc.value().y());
        } else if (dataType.isEnum()) {
            return this.getEnumSettingInput(desc.x() + 1, desc.y() + 1, desc.width() - 1, desc.height() - 2, desc.value().x(), (Enum<?>)desc.value().y());
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

        // Get all relevant settings
        var relevantSettings = gameMode.getRelevantSettings().stream();

        // Get the search text
        var searchText = searchTextField.getText().trim().toLowerCase();

        // If the search text changed
        if (!searchText.equals(previousSearchText)) {

            // Reset the list
            resetList();

            // Set the previous search text
            previousSearchText = searchText;
        }

        // If a search text is given
        if (!searchText.isEmpty()) {

            // Filter
            relevantSettings = relevantSettings.filter(s -> I18n.translate("gui.game_manager.settings." + s.getName()).toLowerCase().contains(searchText));
        }

        // Sort and map
        return relevantSettings
                .sorted(Comparator.comparing(SettingDescription::getName))
                .map(s -> {

                    Object value;
                    if (s.getDataType().isEnum()) {
                        value = settingsManager.getEnum(s);
                    } else {
                        value = settingsManager.get(s);
                    }

                    var translatableName = s.isGeneral() ? "gui.game_manager.settings.group_header.general" : "gui.game_manager.settings.group_header.game_mode_specific";

                    return new SettingRowType(s, value, translatableName);
                }).toList();
    }

    /**
     * Get the template for a boolean setting value
     *
     * @param x The start x-value of the cell
     * @param y The start y-value of the cell
     * @param width The width of the cell
     * @param height The height of the cell
     * @param setting The setting description
     * @param initialValue The initial value of the setting
     * @return The cell template
     */
    private Drawable getBooleanSettingInput(int x, int y, int width, int height, SettingDescription setting, boolean initialValue) {

        if (player.hasPermissionLevel(4)) {
            var buttonWidet = new YesNoButtonWidget(x, y, width, height, initialValue, (newValue) -> {
                // Create packet buffer
                var buf = new PacketByteBuf(Unpooled.buffer());

                buf.writeString(setting.toString());
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
     * @param setting The setting description
     * @param initialValue The initial value of the setting
     * @return The cell template
     */
    private Drawable getLongSettingInput(int x, int y, int width, int height, SettingDescription setting, long initialValue) {

        var tooltipText = Text.translatable("gui.game_manager.settings." + setting.getName() + ".value_unit");

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
                    var settingDescription = SettingDescription.valueOf(setting.toString());

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

                    buf.writeString(setting.toString());
                    buf.writeLong(newLongValue);

                    ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_SETTING_CHANGE, buf);
                } catch (NumberFormatException ignored) {
                    // Do nothing
                }
            });

            textFieldWidget.setTooltip(tooltipText);

            return textFieldWidget;
        } else {
            var startY = y + (height / 2) - (this.textRenderer.fontHeight / 2);
            return new LabelWidget(x + 5, startY, this.textRenderer, Text.literal(Long.toString(initialValue)), tooltipText);
        }
    }

    private Drawable getEnumSettingInput(int x, int y, int width, int height, SettingDescription setting, Enum<?> initialValue) {
        if (player.hasPermissionLevel(4)) {

            // Get the setting description
            var settingDescription = SettingDescription.valueOf(setting.toString());

            // Get the enum values
            var enumValues = settingDescription.getDataType().getValueType().getEnumConstants();

            return new CyclingValueButtonWidget<>(x, y, width, height, initialValue,
                    value -> Text.translatable(setting + "." + ((Enum<?>)value).name()),
                    Arrays.stream(enumValues).toList(),
                    value -> {
                        // Get the enum value as a string
                        var enumValueString = ((Enum<?>)value).name();

                        // Create packet buffer
                        var buf = new PacketByteBuf(Unpooled.buffer());

                        buf.writeString(setting.toString());
                        buf.writeString(enumValueString);

                        ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_SETTING_CHANGE, buf);
                    });
        } else {
            var startY = y + (height / 2) - (this.textRenderer.fontHeight / 2);
            return new LabelWidget(x + 5, startY, this.textRenderer, Text.translatable(setting.toString() + "." + initialValue.name()));
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

    private record SettingRowType(SettingDescription x, Object y, String group) {}
}

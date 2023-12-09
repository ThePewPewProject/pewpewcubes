package de.kleiner3.lasertag.client.screen;

import de.kleiner3.lasertag.client.screen.widget.*;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ArenaType;
import de.kleiner3.lasertag.worldgen.chunkgen.type.ProceduralArenaType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * The arena management screen of the lasertag game manager
 *
 * @author Étienne Muser
 */
public class LasertaGameManagerArenaScreen extends GameManagerScreen {

    /**
     * Extension to the procedural arena names to distinguish them from all other arena types
     */
    private static final String PROCEDURAL_NAME_EXTENSION = "_extended";

    private ListWidget<String, String> list;

    public LasertaGameManagerArenaScreen(Screen parent, PlayerEntity player) {
        super(parent, "gui.game_manager.map_screen_title", player);
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
    protected void init() {
        super.init();

        var columns = new ArrayList<ListColumn<String, String>>(2);

        columns.add(new ListColumn<>(this::getArenaNameColumn, s -> s, 5));
        columns.add(new ListColumn<>(this::getLoadArenaColumn, s -> s, 1));

        var columnsDefinition = new ListColumnsDefinition<>(columns);

        var availableHeight = this.height - (2 * verticalPadding + this.textRenderer.fontHeight + 2 * buttonPadding + buttonHeight);

        this.list = this.addDrawableChild(ListWidget.fromAvailableHeight(horizontalPadding, verticalPadding + textRenderer.fontHeight + buttonPadding,
                this.width - 2 * horizontalPadding, availableHeight,
                this::getArenaNames,
                columnsDefinition, this, this.textRenderer));
    }

    /**
     * Get the template for the name column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getArenaNameColumn(ListCell<String> desc) {
        var startY = desc.y() + (desc.height() / 2) - (this.textRenderer.fontHeight / 2);
        return new LabelWidget(desc.x() + 5, startY, this.textRenderer, Text.translatable(desc.value()));
    }

    /**
     * Get the template for the load column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getLoadArenaColumn(ListCell<String> desc) {
        return new ButtonWidget(desc.x() + 1, desc.y() + 1, desc.width() - 2, desc.height() - 2, Text.translatable("gui.load"), button -> {

            this.client.setScreen(null);

            // Get arena translatable name
            var arenaName = desc.value();

            if (arenaName.endsWith(PROCEDURAL_NAME_EXTENSION)) {
                arenaName = arenaName.substring(0, arenaName.length() - PROCEDURAL_NAME_EXTENSION.length());
            }

            // Create packet buffer
            var buf = new PacketByteBuf(Unpooled.buffer());

            buf.writeString(arenaName);

            ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_LOAD_MAP, buf);
        });
    }

    /**
     * The data source for the list
     *
     * @return The arena names
     */
    private List<String> getArenaNames() {
        return Arrays.stream(ArenaType.values())
                .flatMap(arenaType -> {
                    if (arenaType == ArenaType.PROCEDURAL) {
                        return Arrays.stream(ProceduralArenaType.values()).map(proceduralArenaType -> proceduralArenaType.translatableName + PROCEDURAL_NAME_EXTENSION);
                    } else {
                        return Stream.of(arenaType.translatableName);
                    }
                })
                .toList();
    }
}

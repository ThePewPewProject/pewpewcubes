package de.kleiner3.lasertag.client.screen;

import de.kleiner3.lasertag.client.screen.widget.*;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamConfigManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * The team selector main screen
 *
 * @author Étienne Muser
 */
public class LasertagTeamSelectorScreen extends Screen {

    private static final Text TITLE = Text.translatable("gui.team_selector.title");
    private static final int VERTICAL_PADDING = 15;
    private static final int BUTTON_HEIGHT = 20;
    private static final int EXIT_BUTTON_WIDTH = 100;
    private static final int HORIZONTAL_PADDING = 20;
    private static final int BUTTON_PADDING = 10;

    private ListWidget<TeamDto, Integer> list;

    private final PlayerEntity player;

    public LasertagTeamSelectorScreen(PlayerEntity player) {
        super(TITLE);

        this.player = player;
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
    protected void init() {
        super.init();

        var columns = new ArrayList<ListColumn<TeamDto, Integer>>();

        columns.add(new ListColumn<>(this::getTeamNameColumn, TeamDto::id, 2));
        columns.add(new ListColumn<>(this::getJoinTeamButtonColumn, TeamDto::id, 1));

        var columnsDefinition = new ListColumnsDefinition<>(columns);

        var availableHeight = this.height - (2 * VERTICAL_PADDING + this.textRenderer.fontHeight + 2 * BUTTON_PADDING + BUTTON_HEIGHT);

        this.list = this.addDrawableChild(ListWidget.fromAvailableHeight(HORIZONTAL_PADDING, VERTICAL_PADDING + textRenderer.fontHeight + BUTTON_PADDING,
                this.width - 2 * HORIZONTAL_PADDING, availableHeight,
                this::getTeams,
                columnsDefinition, this, this.textRenderer));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.drawBackground(matrices);
        DrawableHelper.drawCenteredText(matrices, this.textRenderer, TITLE.getString(), this.width / 2, VERTICAL_PADDING + (this.textRenderer.fontHeight / 2), 0xFFFFFFFF);
        drawExitButton();

        super.render(matrices, mouseX, mouseY, delta);
    }

    /**
     * Get the template for the team name column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getTeamNameColumn(ListCell<TeamDto> desc) {

        // Get the team of the player
        var playersTeam = LasertagGameManager.getInstance().getTeamManager().getTeamOfPlayer(this.player.getUuid());

        var teamDto = desc.value();
        var teamName = teamDto.name();
        if (playersTeam.isPresent() && playersTeam.get().equals(teamDto)) {
            teamName = "Your team: " + teamName;
        }

        var teamColor = teamDto.color().getValue();
        var startY = desc.y() + (desc.height() / 2) - (this.textRenderer.fontHeight / 2);

        return new LabelWidget(desc.x() + 5, startY, this.textRenderer, Text.literal(teamName), teamColor);
    }

    /**
     * Get the template for the join team button column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getJoinTeamButtonColumn(ListCell<TeamDto> desc) {
        return new ButtonWidget(desc.x() + 1, desc.y() + 1, desc.width() - 2, desc.height() - 2, Text.translatable("gui.team_selector.join_team"), button -> {
            // Create packet buffer
            var buf = new PacketByteBuf(Unpooled.buffer());

            buf.writeInt(desc.value().id());

            ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_PLAYER_JOIN_TEAM, buf);
        });
    }

    /**
     * The data source for the list
     *
     * @return The teams
     */
    private List<TeamDto> getTeams() {
        return LasertagGameManager.getInstance().getTeamManager().getTeamConfigManager().teamConfig.values().stream()
                // Sort spectators to the bottom
                .sorted((teamA, teamB) -> {
                    if (teamA.equals(TeamConfigManager.SPECTATORS)) {
                        return 1;
                    }

                    if (teamB.equals(TeamConfigManager.SPECTATORS)) {
                        return -1;
                    }

                    return 0;
                })
                .toList();
    }

    private void drawBackground(MatrixStack matrices) {
        DrawableHelper.fill(matrices, 0, 0, this.width, this.height, 0x80000000);
    }

    private void drawExitButton() {
        this.addDrawableChild(new ButtonWidget(this.width - HORIZONTAL_PADDING - EXIT_BUTTON_WIDTH, this.height - VERTICAL_PADDING - BUTTON_HEIGHT, EXIT_BUTTON_WIDTH, BUTTON_HEIGHT, Text.translatable("gui.exit"), button -> {
            this.close();
        }));
    }
}

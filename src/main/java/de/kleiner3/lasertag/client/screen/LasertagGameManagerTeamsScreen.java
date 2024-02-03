package de.kleiner3.lasertag.client.screen;

import de.kleiner3.lasertag.client.screen.widget.*;
import de.kleiner3.lasertag.common.types.Tuple;
import de.kleiner3.lasertag.lasertaggame.team.TeamDto;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * The teams screen of the lasertag game manager
 *
 * @author Étienne Muser
 */
public class LasertagGameManagerTeamsScreen extends GameManagerScreen {

    private ListWidget<Tuple<TeamDto, UUID>, UUID> list;

    public LasertagGameManagerTeamsScreen(Screen parent, PlayerEntity player) {
        super(parent, "gui.game_manager.teams_screen_title", player);
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

        var columns = new ArrayList<ListColumn<Tuple<TeamDto, UUID>, UUID>>();

        columns.add(new ListColumn<>(this::getTeamNameColumn, Tuple::y, 2));
        columns.add(new ListColumn<>(this::getPlayerNameColumn, Tuple::y, 2));
        columns.add(new ListColumn<>(this::getKickPlayerColumn, Tuple::y, 1));

        var columnsDefinition = new ListColumnsDefinition<>(columns);

        var availableHeight = this.height - (2 * verticalPadding + this.textRenderer.fontHeight + 2 * buttonPadding + buttonHeight);

        this.list = this.addDrawableChild(ListWidget.fromAvailableHeight(horizontalPadding, verticalPadding + textRenderer.fontHeight + buttonPadding,
                this.width - 2 * horizontalPadding, availableHeight,
                this::getPlayers,
                columnsDefinition, this, this.textRenderer));
    }

    /**
     * Get the template for the team name column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getTeamNameColumn(ListCell<Tuple<TeamDto, UUID>> desc) {
        var teamDto = desc.value().x();
        var teamName = this.getTeamName(teamDto);
        var teamColor = teamDto != null ? teamDto.color().getValue() : 0xFFFFFFFF;
        var startY = desc.y() + (desc.height() / 2) - (this.textRenderer.fontHeight / 2);
        return new LabelWidget(desc.x() + 5, startY, this.textRenderer, Text.literal(teamName), teamColor);
    }

    /**
     * Get the template for the player name column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getPlayerNameColumn(ListCell<Tuple<TeamDto, UUID>> desc) {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var playerUsernamesState = gameManager.getSyncedState().getPlayerNamesState();

        var playerName = playerUsernamesState.getPlayerUsername(desc.value().y());
        var startY = desc.y() + (desc.height() / 2) - (this.textRenderer.fontHeight / 2);
        var networkPlayer = this.client.getNetworkHandler().getPlayerListEntry(desc.value().y());
        var playerColor = networkPlayer != null ? 0xFFFFFFFF : 0xFF808080;
        return new LabelWidget(desc.x(), startY, this.textRenderer, Text.literal(playerName), playerColor);
    }

    /**
     * Get the template for the kick player button column of the list
     *
     * @param desc The cell description
     * @return The cell template
     */
    private Drawable getKickPlayerColumn(ListCell<Tuple<TeamDto, UUID>> desc) {
        var buttonWidget = new ButtonWidget(desc.x() + 1, desc.y() + 1, desc.width() - 2, desc.height() - 2, Text.translatable("gui.kick"), button -> {
            // Create packet buffer
            var buf = new PacketByteBuf(Unpooled.buffer());

            buf.writeUuid(desc.value().y());

            ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_PLAYER_KICK, buf);
        });

        buttonWidget.active = desc.value().x() != null;

        return buttonWidget;
    }

    /**
     * The data source for the list
     *
     * @return The teams and players
     */
    private List<Tuple<TeamDto, UUID>> getPlayers() {

        // Get the game managers
        var gameManager = MinecraftClient.getInstance().world.getClientLasertagManager();
        var playerNamesState = gameManager.getSyncedState().getPlayerNamesState();
        var teamsManager = gameManager.getTeamsManager();
        var teamsConfigState = gameManager.getSyncedState().getTeamsConfigState();

        var players = new ArrayList<Tuple<TeamDto, UUID>>();

        playerNamesState.forEachPlayer(playerUuid -> {

            var teamIdOptional = teamsManager.getTeamOfPlayer(playerUuid);
            TeamDto team = null;
            if (teamIdOptional.isPresent()) {
                var teamId = teamIdOptional.get();
                team = teamsConfigState.getTeamOfId(teamId).orElseThrow();
            }
            players.add(new Tuple<>(team, playerUuid));
        });

        // All those Z's are to make sure that players without teams get sorted to the bottom of the list
        return players.stream().sorted(Comparator.comparing(t -> t.x() != null ? t.x().name() : "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ")).toList();
    }

    private String getTeamName(TeamDto teamDto) {
        return teamDto != null ? teamDto.name() : "";
    }
}

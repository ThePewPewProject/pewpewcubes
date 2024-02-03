package de.kleiner3.lasertag.client.screen;

import de.kleiner3.lasertag.lasertaggame.gamemode.GameModes;
import de.kleiner3.lasertag.networking.NetworkingConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

/**
 * The main screen of the lasertag game manager
 *
 * @author Étienne Muser
 */
public class LasertagGameManagerScreen extends GameManagerScreen {

    private CyclingButtonWidget<String> gameModeButton;

    public LasertagGameManagerScreen(PlayerEntity player) {
        super(null, "gui.game_manager.overview_screen_title", player);
    }

    public void reloadGameMode() {

        // Get the game managers
        var gameManager = client.world.getClientLasertagManager();
        var gameModeManager = gameManager.getGameModeManager();

        this.gameModeButton.setValue(gameModeManager.getGameMode().getTranslatableName());
    }

    @Override
    protected void init() {
        super.init();

        // Get the game managers
        var gameManager = client.world.getClientLasertagManager();
        var gameModeManager = gameManager.getGameModeManager();

        this.gameModeButton = this.addListedWidget((x, y, width, height) -> CyclingButtonWidget
                .builder(this::getGameModeText)
                .values(GameModes.GAME_MODES.keySet())
                .initially(gameModeManager.getGameMode().getTranslatableName())
                .build(x, y, width, height, Text.translatable("gui.game_manager.game_mode_button"),
                (button, gameModeTranslatableName) -> {

                    // Send event to server
                    var buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeString(gameModeTranslatableName);
                    ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_GAME_MODE_CHANGE, buf);
                }));
        this.gameModeButton.active = this.player.hasPermissionLevel(4);

        this.addListedButton("gui.game_manager.settings_button", this::onSettingsClick);

        if (this.player.hasPermissionLevel(4)) {
            this.addListedButton("gui.game_manager.map_button", this::onMapClick);
        }
        if (this.player.hasPermissionLevel(4)) {
            this.addListedButton("gui.game_manager.teams_button", this::onTeamsClick);
        }

        if (this.player.hasPermissionLevel(4)) {
            this.addListedButton("gui.game_manager.reset_team_config_button", this::onResetTeamConfigClick);
        }
        if (this.player.hasPermissionLevel(4)) {
            this.addListedButton("gui.game_manager.reload_team_config_button", this::onReloadTeamConfigClick);
        }
    }

    private void onSettingsClick(ButtonWidget button) {
        this.client.setScreen(new LasertagGameManagerSettingsScreen(this, player));
    }

    private void onMapClick(ButtonWidget button) {
        this.client.setScreen(new LasertaGameManagerArenaScreen(this, player));
    }

    private void onTeamsClick(ButtonWidget button) {
        this.client.setScreen(new LasertagGameManagerTeamsScreen(this, player));
    }

    private void onResetTeamConfigClick(ButtonWidget button) {
        ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_RESET_TEAM_CONFIG, PacketByteBufs.empty());
    }

    private void onReloadTeamConfigClick(ButtonWidget button) {
        ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_RELOAD_TEAM_CONFIG, PacketByteBufs.empty());
    }

    private Text getGameModeText(String gameModeTranslatableName) {
        return Text.translatable(gameModeTranslatableName);
    }
}

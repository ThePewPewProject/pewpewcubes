package de.kleiner3.lasertag.client.screen;

import de.kleiner3.lasertag.networking.NetworkingConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;

/**
 * The main screen of the lasertag game manager
 *
 * @author Étienne Muser
 */
public class LasertagGameManagerScreen extends GameManagerScreen {

    public LasertagGameManagerScreen(PlayerEntity player) {
        super(null, "gui.game_manager.overview_screen_title", player);
    }

    @Override
    protected void init() {
        super.init();

        this.addListedButton("gui.game_manager.settings_button", this::onSettingsClick);

        if (this.player.hasPermissionLevel(4)) {
            this.addListedButton("gui.game_manager.map_button", this::onMapClick);
        }
        if (this.player.hasPermissionLevel(4)) {
            this.addListedButton("gui.game_manager.teams_button", this::onTeamsClick);
        }

        if (this.player.hasPermissionLevel(4)) {
            this.addListedButton("gui.game_manager.reload_team_config_button", this::onReloadTeamConfigClick);
        }

        if (this.player.hasPermissionLevel(1)) {
            this.addListedButton("gui.game_manager.start_game_button", this::onStartGameClick);
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

    private void onReloadTeamConfigClick(ButtonWidget button) {
        ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_RELOAD_TEAM_CONFIG, PacketByteBufs.empty());
    }

    private void onStartGameClick(ButtonWidget button) {
        this.close();
        ClientPlayNetworking.send(NetworkingConstants.CLIENT_TRIGGER_GAME_START, PacketByteBufs.empty());
    }
}

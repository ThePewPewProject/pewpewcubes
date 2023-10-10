package de.kleiner3.lasertag.networking.client.callbacks;

import de.kleiner3.lasertag.client.screen.LasertagGameManagerTeamsScreen;
import de.kleiner3.lasertag.client.screen.LasertagTeamSelectorScreen;
import de.kleiner3.lasertag.lasertaggame.management.LasertagGameManager;
import de.kleiner3.lasertag.lasertaggame.management.team.TeamDto;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

/**
 * Callback for the team update network event
 *
 * @author Étienne Muser
 */
public class TeamUpdateCallback implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        var playerUuid = buf.readUuid();

        var oldValueString = buf.readString();
        TeamDto oldValue = null;
        if (!oldValueString.equals("null")) {

            oldValue = LasertagGameManager.getInstance().getTeamManager().getTeamConfigManager().getTeamOfId(Integer.parseInt(oldValueString)).get();
        }

        var newValueString = buf.readString();
        TeamDto newValue = null;
        if (!newValueString.equals("null")) {

            newValue = LasertagGameManager.getInstance().getTeamManager().getTeamConfigManager().getTeamOfId(Integer.parseInt(newValueString)).get();
        }

        LasertagGameManager.getInstance().getTeamManager().updateTeam(playerUuid, oldValue, newValue);

        if (client.currentScreen instanceof LasertagGameManagerTeamsScreen lasertagGameManagerTeamsScreen) {
            lasertagGameManagerTeamsScreen.resetList();
        }

        if (client.currentScreen instanceof LasertagTeamSelectorScreen lasertagTeamSelectorScreen) {
            lasertagTeamSelectorScreen.resetList();
        }
    }
}

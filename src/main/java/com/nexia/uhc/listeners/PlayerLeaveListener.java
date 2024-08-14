package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.player.PlayerDisconnectEvent;
import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.game.Game;
import com.nexia.uhc.utility.ChatFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerLeaveListener {
    public void registerListener() {
        PlayerDisconnectEvent.BACKEND.register(event -> {
            Player player = event.getPlayer();
            Game game = NexiaUHC.manager.getGame(player);

            if (game != null) {
                game.getPlayers().remove(player);
            }

            NexiaUHC.lobby.getPlayers().remove(player);
            event.setLeaveMessage(ChatFormat.nexiaMessage.append(player.getName().append(Component.text(" left the game")).color(NamedTextColor.WHITE)));
        });
    }
}

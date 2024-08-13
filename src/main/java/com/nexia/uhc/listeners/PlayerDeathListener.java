package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.player.PlayerDeathEvent;
import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.game.Game;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerDeathListener {
    public void registerListener() {
        PlayerDeathEvent.BACKEND.register(event -> {
            Player player = event.getPlayer();
            Game game = NexiaUHC.manager.getGame(player);

            if (game != null) {
                game.getPlayers().remove(player);
            }

            event.setDeathMessage(event.getDeathMessage().color(NamedTextColor.WHITE));
        });
    }
}

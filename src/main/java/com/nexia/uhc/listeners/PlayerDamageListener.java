package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.entity.LivingEntityDamageEvent;
import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.game.Game;

public class PlayerDamageListener {
    public void registerListener() {
        LivingEntityDamageEvent.BACKEND.register(event -> {
            if (event.getLivingEntity() instanceof Player player) {
                Game game = NexiaUHC.manager.getGame(player);
                if (game != null && !game.isPvpEnabled()) {
                    event.setCancelled(true);
                }

                if (NexiaUHC.lobby.isInLobby(player)) {
                    event.setCancelled(true);
                }
            }
        });
    }
}

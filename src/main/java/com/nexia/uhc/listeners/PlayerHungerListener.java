package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.player.PlayerFoodLevelsChangeEvent;
import com.nexia.uhc.NexiaUHC;

public class PlayerHungerListener {
    public void registerListener() {
        PlayerFoodLevelsChangeEvent.BACKEND.register(event -> {
            if (NexiaUHC.lobby.isInLobby(event.getPlayer())) {
                event.setCancelled(true);
            }
        });
    }
}

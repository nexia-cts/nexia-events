package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.player.PlayerMoveEvent;
import com.nexia.uhc.NexiaUHC;

public class PlayerMoveListener {
    public void registerListener() {
        PlayerMoveEvent.BACKEND.register(event -> {
            if (!NexiaUHC.lobby.isInLobby(event.getPlayer())) return;

            if (event.getNewPosition().getY() < -5) {
                event.setNewPosition(NexiaUHC.lobby.getSpawn());
            }
        });
    }
}

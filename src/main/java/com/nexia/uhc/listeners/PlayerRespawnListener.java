package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.player.PlayerRespawnEvent;
import com.nexia.nexus.api.world.types.Minecraft;
import com.nexia.uhc.NexiaUHC;

public class PlayerRespawnListener {
    public void registerListener() {
        PlayerRespawnEvent.BACKEND.register(event -> {
            NexiaUHC.lobby.returnToLobby(event.getPlayer(), Minecraft.GameMode.SURVIVAL, true);
            event.setRespawnMode(Minecraft.GameMode.SURVIVAL);
            event.setSpawnpoint(NexiaUHC.lobby.getSpawn());
            event.setSpawnpointForced(true);
        });
    }
}

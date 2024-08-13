package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.player.PlayerBreakBlockEvent;
import com.nexia.nexus.api.event.player.PlayerChangeBlockStateEvent;
import com.nexia.nexus.api.event.player.PlayerInteractBlockEvent;
import com.nexia.nexus.api.world.types.Minecraft;
import com.nexia.uhc.NexiaUHC;

public class PlayerInteractListener {
    public void registerListener() {
        PlayerBreakBlockEvent.BACKEND.register(event -> {
            if (!NexiaUHC.lobby.isInLobby(event.getPlayer()) || event.getPlayer().getGameMode() == Minecraft.GameMode.CREATIVE) return;
            event.setCancelled(true);
        });

        PlayerInteractBlockEvent.BACKEND.register(event -> {
            if (!NexiaUHC.lobby.isInLobby(event.getPlayer()) || event.getPlayer().getGameMode() == Minecraft.GameMode.CREATIVE) return;
            event.setCancelled(true);
        });

        PlayerChangeBlockStateEvent.BACKEND.register(event -> {
            if (!NexiaUHC.lobby.isInLobby(event.getPlayer()) || event.getPlayer().getGameMode() == Minecraft.GameMode.CREATIVE) return;
            event.setCancelled(true);
        });
    }
}

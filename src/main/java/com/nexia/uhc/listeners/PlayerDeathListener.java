package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.player.PlayerDeathEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerDeathListener {
    public void registerListener() {
        PlayerDeathEvent.BACKEND.register(event -> event.setDeathMessage(event.getDeathMessage().color(NamedTextColor.WHITE)));
    }
}

package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.player.PlayerJoinEvent;
import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.nexus.api.world.types.Minecraft;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.utility.ChatFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;

public class PlayerJoinListener {
    public void registerListener() {
        PlayerJoinEvent.BACKEND.register(event -> {
            Player player = event.getPlayer();
            player.teleport(NexiaUHC.lobby.getSpawn());

            NexiaUHC.lobby.returnToLobby(player, Minecraft.GameMode.SURVIVAL, true);
            player.sendTitle(Title.title(Component.text("Welcome to"), Component.text("Nexia UHC")));

            event.setJoinMessage(ChatFormat.nexiaMessage.append(event.getJoinMessage().color(NamedTextColor.WHITE)));
            player.playSound(Minecraft.Sound.CAT_STRAY_AMBIENT, 1, 1);
        });
    }
}

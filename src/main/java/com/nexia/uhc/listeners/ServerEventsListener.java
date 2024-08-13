package com.nexia.uhc.listeners;

import com.nexia.nexus.api.event.server.ServerStartupFinishEvent;
import com.nexia.nexus.api.event.server.ServerStopEvent;
import com.nexia.nexus.api.event.server.ServerTickEvent;
import com.nexia.nexus.api.world.scoreboard.Scoreboard;
import com.nexia.nexus.api.world.scoreboard.ScoreboardObjective;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.utility.ChatFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class ServerEventsListener {
    public void registerListener() {
        ServerStartupFinishEvent.BACKEND.register(event -> {
            Scoreboard serverScoreboard = event.getServer().getServerScoreboard();
            ScoreboardObjective health = serverScoreboard.getObjective("hp");
            if (health == null) health = serverScoreboard.addObjective("hp", "health");

            serverScoreboard.setDisplayedObjective(health, "belowName");
            health.setDisplayName(Component.text("❤").color(TextColor.fromHexString("#ff2b1c")));
        });

        ServerStopEvent.BACKEND.register(event -> NexiaUHC.manager.getGames().forEach(game -> NexiaUHC.manager.deleteGame(game)));

        ServerTickEvent.BACKEND.register(event -> {
            NexiaUHC.manager.tickAllGames();
            NexiaUHC.lobby.getPlayers().forEach(player -> player.sendActionBarMessage(ChatFormat.nexiaIp));
        });
    }
}

package com.nexia.teams.utilities.time;

import com.nexia.teams.koth.KothGameHandler;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ServerTime {
    public static MinecraftServer minecraftServer = null;
    public static int totalTickCount = -1;

    public static void firstTick(MinecraftServer server) {
        ServerTime.minecraftServer = server;
    }

    public static void stopServer() {
        KothGameHandler.stopAllKothGames();

        try {
            for (ServerPlayer player : ServerTime.minecraftServer.getPlayerList().getPlayers()) {
                player.connection.disconnect(ChatFormat.convertComponent(MiniMessage.miniMessage().deserialize(String.format("<color:%s>Server is restarting!</color>", ChatFormat.Minecraft.red))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void everyTick() {
        totalTickCount++;

        if (totalTickCount % 20 == 0) everySecond();
    }

    public static void everySecond() {
        KothGameHandler.kothGamesSecond();
    }
}

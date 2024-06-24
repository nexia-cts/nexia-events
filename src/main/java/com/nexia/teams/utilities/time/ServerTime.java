package com.nexia.teams.utilities.time;

import com.google.common.base.Suppliers;
import com.nexia.teams.koth.KothGame;
import com.nexia.teams.koth.KothGameHandler;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.data.KothDataManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class ServerTime {
    public static MinecraftServer minecraftServer = null;

    private static int totalTickCount = -1;
    private static int totalSecondCount = -1;

    public static void firstTick(MinecraftServer server) {
        ServerTime.minecraftServer = server;
        ChatFormat.provider = Suppliers.ofInstance(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)).get();
        KothDataManager.loadKothGamesData();
    }

    public static void stopServer(MinecraftServer server) {
        KothGameHandler.stopAllKothGames();
        KothDataManager.saveKothGamesData();

        try {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.connection.disconnect(ChatFormat.convertComponent(MiniMessage.miniMessage().deserialize(String.format("<color:%s>Server is restarting!</color>", ChatFormat.Minecraft.red.asHexString()))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void everyTick(MinecraftServer server) {
        totalTickCount++;

        if (totalTickCount % 20 == 0) {
            everySecond();
        }
    }

    static void everySecond() {
        totalSecondCount++;

        long unixTime = System.currentTimeMillis() / 1000L;
        for (KothGame kothGame : KothGameHandler.kothGames) {
            if (kothGame.scheduledTimestamp == null) continue;
            if (kothGame.scheduledTimestamp == unixTime) {
                kothGame.start();
            }
        }

        KothGameHandler.kothGamesSecond();
    }

    public static ServerLevel getLevelByName(String name) {
        for (ServerLevel serverLevel : minecraftServer.getAllLevels())  {
            if (serverLevel.toString().equals(name)) {
                return serverLevel;
            }
        }

        return null;
    }
}

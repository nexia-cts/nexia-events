package com.nexia.teams.utilities.time;

import com.google.common.base.Suppliers;
import com.nexia.teams.events.koth.KothGame;
import com.nexia.teams.events.koth.KothGameHandler;
import com.nexia.teams.events.meteor.Meteor;
import com.nexia.teams.events.tournament.TournamentFight;
import com.nexia.teams.utilities.CombatUtil;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.data.KothDataManager;
import com.nexia.teams.utilities.data.NexiaDataManager;
import com.nexia.teams.utilities.teams.TeamUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public abstract class ServerTime {
    public static MinecraftServer minecraftServer = null;

    private static int totalTickCount = -1;
    private static int totalSecondCount = -1;

    public static void firstTick(MinecraftServer server) {
        ServerTime.minecraftServer = server;
        ChatFormat.provider = Suppliers.ofInstance(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)).get();
        NexiaDataManager.loadNexiaData();
        KothDataManager.loadKothGamesData();
    }

    public static void stopServer(MinecraftServer server) {
        NexiaDataManager.saveNexiaData();
        KothGameHandler.stopAllKothGames();
        KothDataManager.saveKothGamesData();
        CombatUtil.stop();

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

        TeamUtil.tick(server);
        if (totalTickCount % 20 == 0) {
            everySecond();
        }
    }

    static void everySecond() {
        totalSecondCount++;

        Meteor.second();
        CombatUtil.second();
        TournamentFight.second();

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
            if (serverLevel.dimension().location().toString().equals(name)) {
                return serverLevel;
            }
        }

        return null;
    }
}

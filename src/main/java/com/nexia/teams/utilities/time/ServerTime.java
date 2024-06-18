package com.nexia.teams.utilities.time;

import com.google.common.base.Suppliers;
import com.nexia.teams.utilities.chat.ChatFormat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ServerTime {
    public static MinecraftServer minecraftServer = null;
    public static int totalTickCount = -1;

    public static void firstTick(MinecraftServer server) {
        ServerTime.minecraftServer = server;
        ChatFormat.provider = Suppliers.ofInstance(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)).get();
    }

    public static void stopServer(MinecraftServer server) {
        try {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.connection.disconnect(ChatFormat.convertComponent(MiniMessage.miniMessage().deserialize(String.format("<color:%s>Server is restarting!</color>", ChatFormat.Minecraft.red))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void everyTick(MinecraftServer server) {
        totalTickCount++;
    }
}

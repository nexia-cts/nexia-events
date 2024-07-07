package com.nexia.teams.events.koth;

import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class KothGame {

    public final String creator;

    public final String name; // name for the koth
    public Long scheduledTimestamp;

    public AABB area;
    public BlockPos initialCoordinates;

    public int time = 300; // measured in seconds
    public boolean isRunning = false;
    public ServerLevel level;

    private ServerPlayer winner;
    private final HashMap<ServerPlayer, Integer> playerScores = new HashMap<>();

    public KothGame(@NotNull String creator, String name, Long scheduledTimestamp, AABB area, BlockPos initialCoordinates, ServerLevel level) {
        this.creator = creator;
        this.name = name;
        this.scheduledTimestamp = scheduledTimestamp;
        this.area = area;
        this.initialCoordinates = initialCoordinates;
        this.level = level;
    }


    public void start() {
        if(this.isRunning) return;

        for (ServerPlayer serverPlayer : ServerTime.minecraftServer.getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text(String.format("KOTH has started at (%s, %s, %s)!", this.area.getCenter().x, this.area.getCenter().y, this.area.getCenter().z)))));
        }

        this.isRunning = true;
    }


    public void end(ServerPlayer winner) {
        this.setWinner(winner);

        if (winner != null) {
            ItemStack reward = Items.CHEST.getDefaultInstance();
            reward.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(ResourceKey.create(Registries.LOOT_TABLE, new ResourceLocation("nexia:chests/koth")), level.getSeed()));
            reward.set(DataComponents.CUSTOM_NAME, ChatFormat.convertComponent(MiniMessage.miniMessage().deserialize(String.format("<bold><gradient:%s:%s>KOTH Reward</gradient></bold> <color:%s>(for %s)</color>", ChatFormat.brandColor1, ChatFormat.brandColor2, ChatFormat.Minecraft.gray, winner.getScoreboardName()))));

            List<net.minecraft.network.chat.Component> list2 = new java.util.ArrayList<>();
            list2.add(ChatFormat.convertComponent(MiniMessage.miniMessage().deserialize(String.format("<color:%s>Personalized just for </color><bold><gradient:%s:%s>%s</gradient></bold><color:%s>!</color>", ChatFormat.Minecraft.gray, ChatFormat.brandColor1, ChatFormat.brandColor2, winner.getScoreboardName(), ChatFormat.Minecraft.gray)).decoration(TextDecoration.ITALIC, false)));
            list2.add(ChatFormat.convertComponent(Component.text("Yours to keep ʕっ·ᴥ·ʔっ", ChatFormat.Minecraft.gray).decoration(TextDecoration.ITALIC, false)));
            reward.set(DataComponents.LORE, new ItemLore(list2));

            winner.addItem(reward);

            for (ServerPlayer serverPlayer : ServerTime.minecraftServer.getPlayerList().getPlayers()) {
                serverPlayer.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text(String.format("KOTH winner is %s!", this.getWinner().getScoreboardName())))));
            }
        }

        this.isRunning = false;
        playerScores.clear();
        this.setWinner(null);
        this.initialCoordinates = null;
    }


    public void kothSecond() {
        if(!this.isRunning) return;

        for (Player player : level.getEntities(EntityType.PLAYER, area, o -> o instanceof ServerPlayer player && !player.isDeadOrDying() && player.gameMode.isSurvival())) {
            ServerPlayer serverPlayer = (ServerPlayer) player;

            if (playerScores.containsKey(serverPlayer)) {
                playerScores.put(serverPlayer, playerScores.get(serverPlayer) + 1);

                if (playerScores.get(serverPlayer) == time) {
                    this.end(serverPlayer);
                }
            } else {
                playerScores.put(serverPlayer, 1);
            }
            
            serverPlayer.sendSystemMessage(ChatFormat.convertComponent(
                    Component.text("Your score » ", ChatFormat.Minecraft.gray)
                            .append(Component.text(playerScores.get(serverPlayer), ChatFormat.Minecraft.red))
                            .append(Component.text(" | ", ChatFormat.Minecraft.dark_gray))
                            .append(Component.text(getWinningPlayer().getScoreboardName() + " (winning)'s score » ", ChatFormat.Minecraft.gray))
                            .append(Component.text(Collections.max(playerScores.values()), ChatFormat.Minecraft.red))
            ), true);

        }
    }

    public ServerPlayer getWinningPlayer() {
        if (this.isRunning) {
            for (Map.Entry<ServerPlayer, Integer> playerEntry : playerScores.entrySet()) {
                if (Objects.equals(playerEntry.getValue(), Collections.max(playerScores.values()))) {
                    return playerEntry.getKey();
                }
            }
        }
        return null;
    }

    public ServerPlayer getWinner() {
        return this.winner;
    }

    public void setWinner(ServerPlayer winner) {
        this.winner = winner;
    }

    public ServerPlayer getCreator() {
        return ServerTime.minecraftServer.getPlayerList().getPlayerByName(this.creator);
    }
}

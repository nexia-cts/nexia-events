package com.nexia.teams.koth;

import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KothGame {

    private final UUID creator;

    public final String name; // name for the koth
    public Long scheduledTimestamp;

    public AABB area;
    public BlockPos initialCoordinates;

    public int time = 300;
    public int timeLeft = 300; // measured in seconds default is five minutes
    public boolean isRunning;
    private ServerLevel level;

    private ServerPlayer winner;
    private final HashMap<ServerPlayer, Integer> playerScores = new HashMap<>();

    public KothGame(@NotNull ServerPlayer creator, String name, Long scheduledTimestamp, AABB area, BlockPos initialCoordinates, ServerLevel level) {
        this.creator = creator.getUUID();
        this.name = name;
        this.scheduledTimestamp = scheduledTimestamp;
        this.area = area;
        this.initialCoordinates = initialCoordinates;
        this.level = level;
    }


    public void start() {
        if(this.isRunning) return;

        // TODO probably add checks to see that the position isn't null lol
        for (ServerPlayer serverPlayer : ServerTime.minecraftServer.getPlayerList().getPlayers()) {
            serverPlayer.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text(String.format("KOTH has started at (%s, %s, %s)!", this.area.getCenter().x, this.area.getCenter().y, this.area.getCenter().z)))));
        }

        this.isRunning = true;
    }


    public void end() {
        for (ServerPlayer serverPlayer : ServerTime.minecraftServer.getPlayerList().getPlayers()) {
            Integer max = Collections.max(playerScores.values());

            for (Map.Entry<ServerPlayer, Integer> entry : playerScores.entrySet()) {
                if (entry.getValue() == max) {
                    this.setWinner(entry.getKey());
                }
            }

            serverPlayer.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text(String.format("KOTH winner is %s!", this.getWinner().getScoreboardName())))));
        }

        this.isRunning = false;

        winner.additem

        playerScores.clear();
        this.setWinner(null);
        this.timeLeft = time;
        this.initialCoordinates = null;
    }


    public void kothSecond() {
        if(!this.isRunning) return;
        if (this.timeLeft <= 0) this.end();

        for (ServerPlayer serverPlayer : ServerTime.minecraftServer.getPlayerList().getPlayers()) {
            if (this.area.contains(serverPlayer.getPosition(0.0F)) && this.level == serverPlayer.serverLevel()) {
                if (playerScores.containsKey(serverPlayer)) {
                    playerScores.put(serverPlayer, playerScores.get(serverPlayer) + 1);
                } else {
                    playerScores.put(serverPlayer, 1);
                }
            }
        }

        // TODO: add checks and shit to announce and ykyk
        this.timeLeft--;
    }

    public ServerPlayer getWinner() {
        return this.winner;
    }

    public void setWinner(ServerPlayer winner) {
        this.winner = winner;
    }

    public ServerPlayer getCreator() {
        return ServerTime.minecraftServer.getPlayerList().getPlayer(this.creator);
    }
}

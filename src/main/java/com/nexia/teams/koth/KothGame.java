package com.nexia.teams.koth;

import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class KothGame {

    private final UUID creator;

    public final String name; // name for the koth
    public final LocalDateTime scheduledTimestamp;

    public final AABB area;
    public final BlockPos initialCoordinates;

    public int timeLeft; // measured in seconds
    private boolean isRunning;

    private UUID winner;
    // TODO add a hash map with all player scores

    public KothGame(@NotNull ServerPlayer creator, String name, LocalDateTime scheduledTimestamp, AABB area, BlockPos initialCoordinates) {
        this.creator = creator.getUUID();
        this.name = name;
        this.scheduledTimestamp = scheduledTimestamp;
        this.area = area;
        this.initialCoordinates = initialCoordinates;
    }


    private KothGame start() {
        if(this.isRunning) return this;

        ServerPlayer creator = this.getCreator();
        if(creator != null) {
            creator.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text(String.format("The KOTH game named '%s' has started!", this.name)))));
        }

        this.isRunning = true;
        return this;
    }


    public void endGame() {
        this.isRunning = false;
    }


    public void kothSecond() {
        if(!this.isRunning) return;

        // TODO: add checks and shit to announce and ykyk
        this.timeLeft--;
    }

    public ServerPlayer getWinner() {
        return ServerTime.minecraftServer.getPlayerList().getPlayer(this.winner);
    }

    public void setWinner(ServerPlayer winner) {
        this.winner = winner.getUUID();
    }

    public ServerPlayer getCreator() {
        return ServerTime.minecraftServer.getPlayerList().getPlayer(this.creator);
    }
}

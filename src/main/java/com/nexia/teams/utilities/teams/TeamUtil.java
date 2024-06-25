package com.nexia.teams.utilities.teams;

import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class TeamUtil {
    public static final int timeout = 120;
    private static final HashMap<UUID, TeamInvite> invites = new HashMap<>();

    public static void addInvite(ServerPlayer player, String teamName) {
        invites.put(player.getUUID(), new TeamInvite(teamName));
    }

    public static String getInvitedTeam(ServerPlayer player) {
        TeamInvite invite = invites.get(player.getUUID());
        if (invite != null) return invite.getTeamName();
        return null;
    }

    public static void removeInvite(ServerPlayer player) {
        invites.remove(player.getUUID());
    }


    public static void tick() {
        for (Map.Entry<UUID, TeamInvite> entry : invites.entrySet()) {
            TeamInvite invite = entry.getValue();

            if (invite.isExpired()) {
                UUID playerUuid = entry.getKey();
                invites.remove(playerUuid);
                ServerPlayer player = ServerTime.minecraftServer.getPlayerList().getPlayer(playerUuid);
                if (player != null) {
                    player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Your invite has expired!"))));
                }
            }

            invite.hasTicked();
        }
    }
}
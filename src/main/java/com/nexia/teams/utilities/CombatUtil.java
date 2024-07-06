package com.nexia.teams.utilities;

import com.nexia.teams.utilities.time.ServerTime;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class CombatUtil {

    public static final Logger logger = LoggerFactory.getLogger(CombatUtil.class);

    public static HashMap<UUID, Integer> combatLoggedPlayersTimer = new HashMap<>();

    public static void second() {
        try {
            CombatUtil.combatLoggedPlayersTimer.forEach(((uuid, integer) -> {
                ServerPlayer serverPlayer = getPlayer(uuid);
                int newInt = integer - 1;
                
                combatLoggedPlayersTimer.replace(uuid, newInt);
                if(serverPlayer == null) {
                    combatLoggedPlayersTimer.remove(uuid);
                    return;
                }
                if(newInt > 0) return;

                CombatUtil.logger.debug("Removing fake player {}: Combat Timer ran out", serverPlayer.getScoreboardName());

                //serverPlayer.connection.disconnect(ChatFormat.convertComponent(Component.text("Combat Log Timer ran out. Disconnecting Bot\n\nIf you are not a bot then please report this issue to the developers.")));
                serverPlayer.addTag("leavekill");
                serverPlayer.kill();

                combatLoggedPlayersTimer.remove(uuid);
            }));
        } catch (Exception ignored) { }

    }

    public static void stop() {
        CombatUtil.logger.debug("Clearing all fake players.");
        combatLoggedPlayersTimer.clear();
    }

    public static boolean addPlayer(ServerPlayer player) {
        CombatUtil.logger.debug("Spawning player {} as a fake player.", player.getScoreboardName());

        ServerTime.minecraftServer.getCommands().performPrefixedCommand(ServerTime.minecraftServer.createCommandSourceStack().withPermission(4), "player " + player.getScoreboardName() + " shadow");

        combatLoggedPlayersTimer.put(player.getUUID(), 30);

        return getPlayer(player.getUUID()) != null;
    }

    private static @Nullable ServerPlayer getPlayer(UUID uuid) {
        return ServerTime.minecraftServer.getPlayerList().getPlayer(uuid);
    }
}


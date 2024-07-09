package com.nexia.teams;

import com.nexia.teams.commands.CommandLoader;
import com.nexia.teams.events.tournament.TournamentFight;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NexiaTeams implements ModInitializer {
    public static final String MOD_ID = "nexia-teams";
    public static Logger logger = LoggerFactory.getLogger(MOD_ID);
    public static boolean pvpEnabled = false;
    public static boolean endDisabled = true;
    public static boolean netherDisabled = true;
    public static boolean hardcoreEnabled = false;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(ServerTime::firstTick);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerTime::stopServer);
        ServerTickEvents.END_SERVER_TICK.register(ServerTime::everyTick);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();

            player.sendSystemMessage(ChatFormat.convertComponent(ChatFormat.nexiaMessage.append(Component.text("Welcome to Nexia Teams!"))));
        });

        ServerLivingEntityEvents.AFTER_DEATH.register(((entity, damageSource) -> {
            if (entity instanceof ServerPlayer serverPlayer) {
                if (TournamentFight.isRunning) {
                    serverPlayer.setGameMode(GameType.SURVIVAL);
                    ServerTime.minecraftServer.getCommands().performPrefixedCommand(ServerTime.minecraftServer.createCommandSourceStack().withPermission(4), "protect remove spawn player " + serverPlayer.getScoreboardName());
                    TournamentFight.serverPlayers.remove(serverPlayer);

                    if (TournamentFight.serverPlayers.stream().noneMatch(o -> o.getTeam() == TournamentFight.blueTeam)) {
                        TournamentFight.end();
                    }

                    if (TournamentFight.serverPlayers.stream().noneMatch(o -> o.getTeam() == TournamentFight.redTeam)) {
                        TournamentFight.end();
                    }
                }
            }
        }));

        logger.info("Loading mod...");
        logger.info("Registering commands...");
        CommandLoader.registerCommands();
        logger.info("Registered commands.");
    }
}


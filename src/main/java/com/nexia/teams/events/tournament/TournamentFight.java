package com.nexia.teams.events.tournament;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class TournamentFight {
    public static boolean isRunning = false;
    public static boolean isStarting = false;
    public static int startTime = 3;
    public static int currentStartTime = 3;
    public static PlayerTeam redTeam = null;
    public static PlayerTeam blueTeam = null;
    public static List<ServerPlayer> serverPlayers = new ArrayList<>();
    public static double[] redSpawn;
    public static double[] blueSpawn;
    public static double[] spectatorSpawn;
    public static AABB spawnArea;
    public static AABB redGate1;
    public static AABB redGate2;
    public static AABB blueGate1;
    public static AABB blueGate2;

    public static void loadTournamentConfig() {
        try {
            String directory = FabricLoader.getInstance().getConfigDir().toString() + "/nexia/tournament";

            String json = Files.readString(Path.of(directory + "/config.json"));
            Gson gson = new Gson();
            TournamentConfig tournamentConfig = gson.fromJson(json, TournamentConfig.class);

            redSpawn = tournamentConfig.redSpawn;
            blueSpawn = tournamentConfig.blueSpawn;
            spectatorSpawn = tournamentConfig.spectatorSpawn;
            spawnArea = new AABB(tournamentConfig.spawnArea[0], tournamentConfig.spawnArea[1], tournamentConfig.spawnArea[2], tournamentConfig.spawnArea[3], tournamentConfig.spawnArea[4], tournamentConfig.spawnArea[5]);
            redGate1 = new AABB(tournamentConfig.redGate1[0], tournamentConfig.redGate1[1], tournamentConfig.redGate1[2], tournamentConfig.redGate1[3], tournamentConfig.redGate1[4], tournamentConfig.redGate1[5]);
            redGate2 = new AABB(tournamentConfig.redGate2[0], tournamentConfig.redGate2[1], tournamentConfig.redGate2[2], tournamentConfig.redGate2[3], tournamentConfig.redGate2[4], tournamentConfig.redGate2[5]);
            blueGate1 = new AABB(tournamentConfig.blueGate1[0], tournamentConfig.blueGate1[1], tournamentConfig.blueGate1[2], tournamentConfig.blueGate1[3], tournamentConfig.blueGate1[4], tournamentConfig.blueGate1[5]);
            blueGate2 = new AABB(tournamentConfig.blueGate2[0], tournamentConfig.blueGate2[1], tournamentConfig.blueGate2[2], tournamentConfig.blueGate2[3], tournamentConfig.blueGate2[4], tournamentConfig.blueGate2[5]);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void clearStuff(AABB area) {
        List<EntityType<? extends Entity>> entityTypes = List.of(EntityType.ITEM, EntityType.ARROW, EntityType.SPECTRAL_ARROW, EntityType.TRIDENT, EntityType.EXPERIENCE_ORB);
        entityTypes.forEach(entityType -> ServerTime.minecraftServer.overworld().getEntities(entityType, area, o -> true).forEach(Entity::kill));
    }

    public static void fillBlocks(AABB area, Block block) {
        Iterable<BlockPos> blocks = BlockPos.betweenClosed((int) area.minX, (int) area.minY, (int) area.minZ, (int) area.maxX, (int) area.maxY, (int) area.maxZ);
        for (BlockPos blockPos : blocks) {
            ServerTime.minecraftServer.overworld().setBlockAndUpdate(blockPos, block.defaultBlockState());
        }
    }

    public static void preStart() {
        loadTournamentConfig();
        clearStuff(spawnArea);

        fillBlocks(redGate1, Blocks.RED_STAINED_GLASS);
        fillBlocks(redGate2, Blocks.RED_STAINED_GLASS);
        fillBlocks(blueGate1, Blocks.CYAN_STAINED_GLASS);
        fillBlocks(blueGate2, Blocks.CYAN_STAINED_GLASS);

        for (ServerPlayer serverPlayer : serverPlayers) {
            if (serverPlayer.getTeam() == redTeam) {
                serverPlayer.teleportTo(ServerTime.minecraftServer.overworld(), redSpawn[0], redSpawn[1], redSpawn[2], 90, 0);
            }

            if (serverPlayer.getTeam() == blueTeam) {
                serverPlayer.teleportTo(ServerTime.minecraftServer.overworld(), blueSpawn[0], blueSpawn[1], blueSpawn[2], -90, 0);
            }

            serverPlayer.setGameMode(GameType.ADVENTURE);
            ServerTime.minecraftServer.getCommands().performPrefixedCommand(ServerTime.minecraftServer.createCommandSourceStack().withPermission(4), "protect exclusion add spawn player " + serverPlayer.getScoreboardName());
        }
    }

    public static void teamTitle(String message, SoundEvent soundEvent) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            try {
                net.minecraft.network.chat.Component component = ChatFormat.convertComponent(Component.text(message, getTitleColor(currentStartTime)));
                Function<net.minecraft.network.chat.Component, ClientboundSetTitleTextPacket> title = ClientboundSetTitleTextPacket::new;
                serverPlayer.connection.send(title.apply(ComponentUtils.updateForEntity(ServerTime.minecraftServer.createCommandSourceStack(), component, serverPlayer, 0)));
                serverPlayer.level().playSound(
                        null,
                        BlockPos.containing(serverPlayer.getPosition(0)),
                        soundEvent,
                        SoundSource.MASTER,
                        1f,
                        1f
                );
            } catch (Exception ignored) {}
        }
    }

    public static TextColor getTitleColor(int currentStartTime) {
        if(currentStartTime == 2) {
            return ChatFormat.Minecraft.yellow;
        } else if(currentStartTime <= 1) {
            return ChatFormat.Minecraft.red;
        }

        return ChatFormat.Minecraft.green;
    }

    public static void start() {
        isStarting = false;
        isRunning = true;
        teamTitle("Go!", SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(2).value());
        fillBlocks(redGate1, Blocks.AIR);
        fillBlocks(blueGate1, Blocks.AIR);
    }

    public static void second() {
        if (isStarting) {
            if (currentStartTime <= 0) {
                start();
                return;
            }
            if (currentStartTime == startTime) preStart();

            teamTitle(String.valueOf(currentStartTime), SoundEvents.NOTE_BLOCK_BIT.value());
            currentStartTime--;
        }

        if (isRunning || isStarting) {
            for (Player player : ServerTime.minecraftServer.overworld().getEntities(EntityType.PLAYER, spawnArea, o -> true)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    if (!serverPlayers.contains(serverPlayer))
                        serverPlayer.teleportTo(spectatorSpawn[0], spectatorSpawn[1], spectatorSpawn[2]);
                }
            }
        }
    }

    public static void end() {
        teamTitle("Game over!", SoundEvents.TRIDENT_THUNDER);
        serverPlayers.forEach(serverPlayer -> {
            serverPlayer.setGameMode(GameType.SURVIVAL);
            ServerTime.minecraftServer.getCommands().performPrefixedCommand(ServerTime.minecraftServer.createCommandSourceStack().withPermission(4), "protect exclusion remove spawn player " + serverPlayer.getScoreboardName());
        });
        isRunning = false;
        isStarting = false;
        redTeam = null;
        blueTeam = null;
        serverPlayers.clear();
        currentStartTime = startTime;
        fillBlocks(redGate2, Blocks.AIR);
        fillBlocks(blueGate2, Blocks.AIR);
    }
}

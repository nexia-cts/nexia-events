package com.nexia.teams.events.tournament;

import com.google.common.base.Function;
import com.nexia.teams.utilities.chat.ChatFormat;
import com.nexia.teams.utilities.time.ServerTime;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;

import java.util.ArrayList;
import java.util.List;

public abstract class TournamentFight {
    public static boolean isRunning = false;
    public static boolean isStarting = false;
    public static int startTime = 3;
    public static int currentStartTime = 3;
    public static PlayerTeam redTeam = null;
    public static PlayerTeam blueTeam = null;
    public static double[] redSpawn = new double[]{24.5, 63, 0.5};
    public static double[] blueSpawn = new double[]{-23.5, 63, 0.5};
    public static List<ServerPlayer> serverPlayers = new ArrayList<>();
    public static AABB spawnArea = new AABB(26, 63, 26, -25, 70, -25);
    public static AABB redGate1 = new AABB(22, 65, -1, 22, 63, 1);
    public static AABB redGate2 = new AABB(26, 65, -1, 26, 63, 1);
    public static AABB blueGate1 = new AABB(-22, 65, -1, -22, 63, 1);
    public static AABB blueGate2 = new AABB(-26, 65, -1, -26, 63, 1);

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

        clearStuff(spawnArea);

        fillBlocks(redGate1, Blocks.MANGROVE_FENCE);
        fillBlocks(redGate2, Blocks.RED_STAINED_GLASS);
        fillBlocks(blueGate1, Blocks.CYAN_STAINED_GLASS);
        fillBlocks(blueGate2, Blocks.CYAN_STAINED_GLASS);

        for (String playerName : redTeam.getPlayers()) {
            ServerPlayer serverPlayer = ServerTime.minecraftServer.getPlayerList().getPlayerByName(playerName);
            if (serverPlayer == null) continue;
            serverPlayers.add(serverPlayer);
            serverPlayer.teleportTo(ServerTime.minecraftServer.overworld(), redSpawn[0], redSpawn[1], redSpawn[2], 90, 0);
        }

        for (String playerName : blueTeam.getPlayers()) {
            ServerPlayer serverPlayer = ServerTime.minecraftServer.getPlayerList().getPlayerByName(playerName);
            if (serverPlayer == null) continue;
            serverPlayers.add(serverPlayer);
            serverPlayer.teleportTo(ServerTime.minecraftServer.overworld(), blueSpawn[0], blueSpawn[1], blueSpawn[2], -90, 0);
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
                if (player.getTeam() != blueTeam && player.getTeam() != redTeam) player.teleportTo(redSpawn[0], 70, redSpawn[2]);
            }
        }
    }

    public static void end() {
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

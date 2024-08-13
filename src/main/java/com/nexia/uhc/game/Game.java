package com.nexia.uhc.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.nexia.nexus.api.world.entity.player.Player;
import com.nexia.nexus.api.world.types.Minecraft;
import com.nexia.nexus.api.world.util.Location;
import com.nexia.nexus.builder.implementation.world.WrappedWorld;
import com.nexia.uhc.NexiaUHC;
import com.nexia.uhc.utility.ChatFormat;
import com.nexia.uhc.utility.MathUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Game {
    String id;
    Path instanceDir;
    List<Player> players;
    WrappedWorld overworld;
    long seed;

    boolean isRunning;
    boolean isPvpEnabled;

    // measured in minecraft ticks
    int timeUntilPvp;
    int timeUntilDeathmatch;

    public Game(List<Player> players) {
        this.id = UUID.randomUUID().toString();
        this.instanceDir = Path.of(NexiaUHC.manager.instancesDir + "/" + this.id);
        this.players = new ArrayList<>(players);
        this.isRunning = false;

        this.isPvpEnabled = false;
        this.timeUntilPvp = 18000;
        this.timeUntilDeathmatch = 18000;
    }

    public boolean isPvpEnabled() {
        return isPvpEnabled;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public WrappedWorld getOverworld() {
        return this.overworld;
    }

    CompletableFuture<Void> prepare() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        long millisecs = System.currentTimeMillis();
        try {
            Files.createDirectories(instanceDir);

            String worldData = Files.readString(Path.of(FabricLoader.getInstance().getConfigDir() + "/nexia-uhc.json"));
            this.seed = NexiaUHC.minecraftServer.overworld().random.nextLong();
            worldData = worldData.replaceAll("null", String.valueOf(this.seed));
            JsonElement worldDataJson = new JsonParser().parse(worldData);
            DataResult<CompoundTag> worldDataNbt = CompoundTag.CODEC.parse(JsonOps.INSTANCE, worldDataJson);
            CompoundTag data = new CompoundTag();
            data.put("Data", worldDataNbt.result().orElseThrow());

            File level = new File(instanceDir.toFile(), "level.dat");
            NbtIo.writeCompressed(data, level);

            NexiaUHC.nexusServer.loadWorldAsync(instanceDir, this.id).thenRun(() -> {
                NexiaUHC.logger.info("Done. (Took {}ms)", System.currentTimeMillis() - millisecs);
                this.overworld = new WrappedWorld(NexiaUHC.minecraftServer.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(this.id + ".minecraft", "overworld"))));
                future.complete(null);
            });
            return future;
        } catch (IOException e) {
            NexiaUHC.logger.error("Error creating world data to instance folder '{}': ", instanceDir.toAbsolutePath(), e);
            future.completeExceptionally(e);
            return future;
        }
    }

    @SuppressWarnings("All")
    public void start() {
        this.prepare().whenComplete((unused, throwable) -> {
            if (throwable == null) {
                ArrayList<int[]> initialPositions = MathUtil.createInitialPositions(new Random(), this.players.size(), -500, 500);
                this.players.forEach(player -> {
                    int[] pos = initialPositions.get(this.players.indexOf(player));
                    int height = this.overworld.unwrap().getChunk(pos[0] >> 4, pos[1] >> 4).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos[0] & 15, pos[1] & 15) + 1;
                    player.teleport(new Location(pos[0] + 0.5, height, pos[1] + 0.5, this.overworld));
                    player.runCommand("loadinventory starter");
                });

                NexiaUHC.taskScheduler.schedule(this::enablePvp, this.timeUntilPvp);
            } else {
                NexiaUHC.logger.error("Error loading UHC world: {}", throwable);
                NexiaUHC.manager.deleteGame(this);
            }
        });
    }

    public void tick() {
        if (this.overworld == null) return;

        if (!isPvpEnabled && timeUntilPvp > 0) {
            this.players.forEach(player -> player.sendActionBarMessage(ChatFormat.nexiaMessage.append(Component.text("Time until PVP: " + MathUtil.convertTicksToTime(timeUntilPvp)).color(NamedTextColor.GRAY))));
            this.timeUntilPvp--;
        }

        if (this.isPvpEnabled && this.timeUntilDeathmatch > 0) {
            this.players.forEach(player -> player.sendActionBarMessage(ChatFormat.nexiaMessage.append(Component.text("Time until death match: " + MathUtil.convertTicksToTime(timeUntilDeathmatch)).color(NamedTextColor.GRAY))));
            this.timeUntilDeathmatch--;
        }

        if (this.players.size() == 1) {
            Player winner = this.players.getFirst();
            this.players.forEach(player -> {
                player.sendTitle(Title.title(winner.getName().color(NamedTextColor.GOLD), Component.text("has won the game!")));
                player.playSound(Minecraft.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            });

            NexiaUHC.taskScheduler.schedule(() -> NexiaUHC.manager.deleteGame(this), 300);
        }
    }

    public void enablePvp() {
        this.isPvpEnabled = true;
        this.players.forEach(player -> {
            player.sendMessage(ChatFormat.nexiaMessage.append(Component.text("PVP has been enabled!")));
            player.playSound(Minecraft.Sound.ENDER_DRAGON_GROWL, 1, 1);
        });
        NexiaUHC.taskScheduler.schedule(this::deathMatch, this.timeUntilDeathmatch);
    }

    @SuppressWarnings("All")
    public void deathMatch() {
        ArrayList<int[]> initialPositions = MathUtil.createInitialPositions(new Random(), this.players.size(), -100, 100);
        this.players.forEach(player -> {
            player.sendMessage(ChatFormat.nexiaMessage.append(Component.text("Death match has started!")));

            int[] pos = initialPositions.get(this.players.indexOf(player));
            int height = this.overworld.unwrap().getChunk(pos[0] >> 4, pos[1] >> 4).getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos[0] & 15, pos[1] & 15) + 1;
            player.teleport(new Location(pos[0] + 0.5, height, pos[1] + 0.5, this.overworld));

            player.playSound(Minecraft.Sound.RAID_HORN, 1, 1);
        });
        this.overworld.unwrap().getWorldBorder().setSize(200);
    }

    public void end() {
        this.players.forEach(player -> {
            player.sendMessage(ChatFormat.nexiaMessage.append(Component.text("World seed: " + this.seed)));
            NexiaUHC.lobby.returnToLobby(player, Minecraft.GameMode.SURVIVAL, true);
        });

        try {
            NexiaUHC.nexusServer.unloadWorld(this.id, false);
            FileUtils.forceDeleteOnExit(instanceDir.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

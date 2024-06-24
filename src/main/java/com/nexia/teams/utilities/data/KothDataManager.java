package com.nexia.teams.utilities.data;

import com.google.gson.Gson;
import com.nexia.teams.koth.KothGame;
import com.nexia.teams.koth.KothGameHandler;
import com.nexia.teams.utilities.time.ServerTime;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.phys.AABB;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class KothDataManager {
    public static String dataDirectory = FabricLoader.getInstance().getConfigDir().toString() + "/nexia/koth";

    public static void saveKothGamesData() {
        try {
            for (KothGame kothGame : KothGameHandler.kothGames) {
                Gson gson = new Gson();
                SavedKothData savedKothData = new SavedKothData(
                        kothGame.creator,
                        kothGame.name,
                        kothGame.scheduledTimestamp,
                        (kothGame.area == null) ? null : new double[]{kothGame.area.minX, kothGame.area.minY, kothGame.area.minZ},
                        (kothGame.area == null) ? null : new double[]{kothGame.area.maxX, kothGame.area.maxY, kothGame.area.maxZ},
                        kothGame.time,
                        kothGame.level.toString()
                );
                String json = gson.toJson(savedKothData);
                String directory = getDataDir();
                FileWriter fileWriter = new FileWriter(directory + "/" + kothGame.name + ".json");
                fileWriter.write(json);
                fileWriter.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void loadKothGamesData() {
        try {
            String directory = getDataDir();

            for (File file : new File(directory).listFiles()) {
                String json = Files.readString(Path.of(directory + "/" + file.getName()));
                Gson gson = new Gson();
                SavedKothData savedKothData = gson.fromJson(json, SavedKothData.class);
                KothGame kothGame = new KothGame(
                        savedKothData.creator,
                        savedKothData.name,
                        savedKothData.scheduledTimeStamp,
                        (savedKothData.firstBlockPos == null || savedKothData.lastBlockPos == null) ? null : new AABB(savedKothData.firstBlockPos[0], savedKothData.firstBlockPos[1], savedKothData.firstBlockPos[2], savedKothData.lastBlockPos[0], savedKothData.lastBlockPos[1], savedKothData.lastBlockPos[2]),
                        null,
                        ServerTime.getLevelByName(savedKothData.level)
                );
                KothGameHandler.kothGames.add(kothGame);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static String getDataDir() {
        (new File(dataDirectory)).mkdirs();
        return dataDirectory;
    }
}

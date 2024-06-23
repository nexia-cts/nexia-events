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

public class KothDataManager {
    public static String dataDirectory = FabricLoader.getInstance().getConfigDir().toString() + "/nexia/koth";

    public static void saveKothGamesData() {
        // TODO add a check for when area is null
        try {
            for (KothGame kothGame : KothGameHandler.kothGames) {
                Gson gson = new Gson();
                SavedKothData savedKothData = new SavedKothData(kothGame.creator, kothGame.name, kothGame.scheduledTimestamp, kothGame.area.minX, kothGame.area.minY, kothGame.area.minZ, kothGame.area.maxX, kothGame.area.maxY, kothGame.area.maxZ, kothGame.time);
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
            // TODO store the minecraft dimension lol
            String directory = getDataDir();
            for (File file : new File(directory).listFiles()) {
                String json = Files.readString(Path.of(directory + "/" + file.getName()));
                Gson gson = new Gson();
                SavedKothData savedKothData = gson.fromJson(json, SavedKothData.class);
                KothGame kothGame = new KothGame(
                        savedKothData.creator,
                        savedKothData.name,
                        savedKothData.scheduledTimeStamp,
                        new AABB(savedKothData.x1, savedKothData.y1, savedKothData.z1, savedKothData.x2, savedKothData.y2, savedKothData.z2),
                        null,
                        null
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

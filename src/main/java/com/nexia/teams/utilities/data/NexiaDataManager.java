package com.nexia.teams.utilities.data;

import com.google.gson.Gson;
import com.nexia.teams.NexiaTeams;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

public class NexiaDataManager {
    public static String dataDirectory = FabricLoader.getInstance().getConfigDir().toString() + "/nexia";

    public static void saveNexiaData() {
        try {
            Gson gson = new Gson();
            SavedNexiaData savedNexiaData = new SavedNexiaData(NexiaTeams.pvpEnabled, NexiaTeams.endDisabled, NexiaTeams.netherDisabled, NexiaTeams.hardcoreEnabled);
            String json = gson.toJson(savedNexiaData);
            String directory = getDataDir();
            FileWriter fileWriter = new FileWriter(directory + "/nexia.json");
            fileWriter.write(json);
            fileWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void loadNexiaData() {
        try {
            String directory = getDataDir();
            File file = new File(directory + "/nexia.json");

            String json = Files.readString(file.toPath());
            Gson gson = new Gson();
            SavedNexiaData savedNexiaData = gson.fromJson(json, SavedNexiaData.class);
            NexiaTeams.pvpEnabled = savedNexiaData.pvpEnabled;
            NexiaTeams.endDisabled = savedNexiaData.endDisabled;
            NexiaTeams.netherDisabled = savedNexiaData.netherDisabled;
            NexiaTeams.hardcoreEnabled = savedNexiaData.hardcoreEnabled;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static String getDataDir() {
        (new File(dataDirectory)).mkdirs();
        return dataDirectory;
    }
}

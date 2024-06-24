package com.nexia.teams.utilities.data;

public class SavedKothData {
    public String creator;
    public String name;
    public Long scheduledTimeStamp;
    public double[] firstBlockPos;
    public double[] lastBlockPos;
    public int time;
    public String level;

    public SavedKothData(String creator, String name, Long scheduledTimeStamp, double[] firstBlockPos, double[] lastBlockPos, int time, String level) {
        this.creator = creator;
        this.name = name;
        this.scheduledTimeStamp = scheduledTimeStamp;
        this.firstBlockPos = firstBlockPos;
        this.lastBlockPos = lastBlockPos;
        this.time = time;
        this.level = level;
    }
}

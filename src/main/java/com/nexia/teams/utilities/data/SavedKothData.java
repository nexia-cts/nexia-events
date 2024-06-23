package com.nexia.teams.utilities.data;

public class SavedKothData {
    public String creator;
    public String name;
    public Long scheduledTimeStamp;
    public double x1;
    public double y1;
    public double z1;
    public double x2;
    public double y2;
    public double z2;
    public int time;

    public SavedKothData(String creator, String name, Long scheduledTimeStamp, double x1, double y1, double z1, double x2, double y2, double z2, int time) {
        this.creator = creator;
        this.name = name;
        this.scheduledTimeStamp = scheduledTimeStamp;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.time = time;
    }
}

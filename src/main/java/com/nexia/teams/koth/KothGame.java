package com.nexia.teams.koth;

public class KothGame {
    String creator; // username of the creator of the koth maybe use uuid?
    String name; // name for the koth
    String scheduledTimestamp;
    int x1;
    int z1;
    int x2;
    int z2;
    boolean isRunning;
    int timeLeft; // probably should be in ticks or seconds
    String winner;
    // TODO add a hash map with all player scores

    public KothGame(String creator, String name, String scheduledTimestamp, int x1, int z1, int x2, int z2) {
        this.creator = creator;
        this.name = name;
        this.scheduledTimestamp = scheduledTimestamp;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
    }

    public void kothSecond() {

    }
}

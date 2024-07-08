package com.nexia.teams.events.tournament;

public class TournamentConfig {
    public double[] redSpawn;
    public double[] blueSpawn;
    public double[] spawnArea;
    public double[] redGate1;
    public double[] redGate2;
    public double[] blueGate1;
    public double[] blueGate2;

    public TournamentConfig(double[] redSpawn, double[] blueSpawn, double[] spawnArea, double[] redGate1, double[] redGate2, double[] blueGate1, double[] blueGate2) {
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.spawnArea = spawnArea;
        this.redGate1 = redGate1;
        this.redGate2 = redGate2;
        this.blueGate1 = blueGate1;
        this.blueGate2 = blueGate2;
    }
}

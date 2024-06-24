package com.nexia.teams.utilities.teams;

import static com.nexia.teams.utilities.teams.TeamUtil.timeout;

public class TeamInvite {
    private final String teamName;
    private int remainingTicks;

    public TeamInvite(String teamName) {
        this.remainingTicks = timeout;
        this.teamName = teamName;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public boolean isExpired() {
        return this.remainingTicks <= 0;
    }

    public void hasTicked() {
        this.remainingTicks--;
    }
}
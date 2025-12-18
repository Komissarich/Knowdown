package com.example.lobby_service.Repositories.Entities;

import java.util.Map;

public class UpgradeRequest {
    private String lobbyId;
    private String username;

    Map<String, Float> upgradedStats;


    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, Float> getUpgradedStats() {
        return upgradedStats;
    }

    public void setUpgradedStats(Map<String, Float> upgradedStats) {
        this.upgradedStats = upgradedStats;
    }

    @Override
    public String toString() {
        return "UpgradeRequest{" +
                "lobbyId='" + lobbyId + '\'' +
                ", username='" + username + '\'' +
                ", upgradedStats=" + upgradedStats +
                '}';
    }
}

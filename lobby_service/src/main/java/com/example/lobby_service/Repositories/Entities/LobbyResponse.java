package com.example.lobby_service.Repositories.Entities;

public class LobbyResponse {
    private final String lobbyId;

    public LobbyResponse(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getLobbyId() {
        return lobbyId;
    }
}

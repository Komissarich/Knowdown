package com.example.lobby_service.Repositories.Entities;

public class isCreatorRequest {
    private String username;
    private String lobby_name;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLobby_name() {
        return lobby_name;
    }

    public void setLobby_name(String lobby_name) {
        this.lobby_name = lobby_name;
    }
}

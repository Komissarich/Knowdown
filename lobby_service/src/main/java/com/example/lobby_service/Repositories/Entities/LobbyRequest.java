package com.example.lobby_service.Repositories.Entities;

public class LobbyRequest {
    private String name;
    private boolean isPrivate;
    private String id;
    private int maxPlayers;
    private String creator;


    public String getName() {
        return name;
    }
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
    public boolean isPrivate() {
        return isPrivate;
    }
    public String getId() {
        return id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}

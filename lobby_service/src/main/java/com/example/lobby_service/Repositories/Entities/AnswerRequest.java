package com.example.lobby_service.Repositories.Entities;

public class AnswerRequest {
    private String name;
    private String lobbyId;
    private Long timestamp;


    public AnswerRequest(String name, String lobbyId, Long timestamp) {
        this.name = name;
        this.lobbyId = lobbyId;
        this.timestamp = timestamp;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


}

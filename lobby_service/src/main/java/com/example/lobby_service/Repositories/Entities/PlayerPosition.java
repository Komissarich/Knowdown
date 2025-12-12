package com.example.lobby_service.Repositories.Entities;

public class PlayerPosition {
    private int x;
    private int y;
    private String playerName;
    private String playerId;
    private String direction;
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "PlayerPosition{" +
                "x=" + x +
                ", y=" + y +
                ", playerName='" + playerName + '\'' +
                ", playerId='" + playerId + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}

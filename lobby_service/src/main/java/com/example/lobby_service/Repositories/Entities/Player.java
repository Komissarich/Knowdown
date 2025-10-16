package com.example.lobby_service.Repositories.Entities;

public class Player {
    public String username;
//    public String id;
    public Player() {}
    public Player( String name) {
//        this.id = id;
        this.username = name;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                '}';
    }
}

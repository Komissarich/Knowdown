package com.example.lobby_service.Repositories.Entities;

public class ChatMessage {
    public String author;
    public String message;

    public ChatMessage() {}

    public ChatMessage(String author, String message) {
        this.author = author;
        this.message = message;
    }
}

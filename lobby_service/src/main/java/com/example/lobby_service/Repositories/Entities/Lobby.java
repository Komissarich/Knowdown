package com.example.lobby_service.Repositories.Entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {

    private List<Player> lobbyPlayers = new ArrayList<Player>();
    private final Set<String> playerNames = new HashSet<>();
    List<ChatMessage> lobbyMessages = new ArrayList<ChatMessage>();

    private String lobbyId;
    private String name;
    private int maxPlayersCount;
    private boolean isPrivate;
    private Player creator;

    public Lobby(boolean isPrivate, int maxPlayersCount, String lobbyId, String name, Player creator) {
        this.isPrivate = isPrivate;
        this.maxPlayersCount = maxPlayersCount;
        this.lobbyId = lobbyId;
        this.creator = creator;
        this.name= name;
    }

    public void AddPlayer(Player player) {
    if (playerNames.add(player.username)) {
        lobbyPlayers.add(player);
    }
    }

    public void RemovePlayer(Player player) {

        this.lobbyPlayers.remove (player);
    }

    public void AddMessage(ChatMessage chatMessage) {

        this.lobbyMessages.add(chatMessage);
    }

    public List<Player> getLobbyPlayers() {
        return lobbyPlayers;
    }

    public void setLobbyPlayers(List<Player> lobbyPlayers) {
        this.lobbyPlayers = lobbyPlayers;
    }

    public List<ChatMessage> getLobbyMessages() {
        return lobbyMessages;
    }

    public void setLobbyMessages(List<ChatMessage> lobbyMessages) {
        this.lobbyMessages = lobbyMessages;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlayersCount() {
        return maxPlayersCount;
    }

    public void setMaxPlayersCount(int maxPlayersCount) {
        this.maxPlayersCount = maxPlayersCount;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }
}

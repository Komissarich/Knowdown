package com.example.lobby_service.Repositories;

import com.example.lobby_service.Repositories.Entities.ChatMessage;
import com.example.lobby_service.Repositories.Entities.Lobby;
import com.example.lobby_service.Repositories.Entities.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Service
public class LobbyService {
    private final Map<String, Lobby> lobbies;

    public LobbyService() {
        lobbies = new ConcurrentHashMap<String, Lobby>();
    }
    public String CreateLobby(boolean isPrivate, int maxPlayersCount, String name, Player creator) {
        String lobbyId = java.util.UUID.randomUUID().toString().substring(0, 6);
        System.out.println("created lobby " + lobbyId);
        Lobby new_lobby = new Lobby(isPrivate, maxPlayersCount, lobbyId, name, creator);
        lobbies.put(lobbyId, new_lobby);
        return lobbyId;
    }

    public List<Player> ListPlayers(String lobbyId) {
        return lobbies.get(lobbyId).getLobbyPlayers();
    }

    public void DeleteLobby(String id) {
        lobbies.remove(id);
    }

    public void AddPlayerInLobby(String id, Player player) {

        lobbies.get(id).AddPlayer(player);
    }

    public void RemovePlayerFromLobby(String id, Player player) {
        lobbies.get(id).RemovePlayer(player);
    }

    public void AddMessage(String id, ChatMessage message) {
        lobbies.get(id).AddMessage(message);

    }

    public boolean checkCreator(String lobbyId, String username) {
        return lobbies.get(lobbyId).getCreator().username.equals(username);
    }
}



package com.example.lobby_service.Repositories;

import com.example.lobby_service.Repositories.Entities.ChatMessage;
import com.example.lobby_service.Repositories.Entities.Lobby;
import com.example.lobby_service.Repositories.Entities.Player;
import com.example.lobby_service.Repositories.Entities.QuestionResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Service
public class LobbyService {
    private final Map<String, Lobby> lobbies;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public LobbyService() {

        lobbies = new ConcurrentHashMap<String, Lobby>();
    }

    public String CreateLobby(boolean isPrivate, int maxPlayersCount, String name, String creator) {
        String lobbyId = UUID.randomUUID().toString().substring(0, 6);
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
        return lobbies.get(lobbyId).getCreator().getUsername().equals(username);
    }

    public void AddAnswer(String name, String lobbyId, Long timestamp) {
        this.lobbies.get(lobbyId).AddAnswer(name, timestamp);
    }

    public Map<String, Integer> sendResults(String lobbyId) {
        System.out.println(lobbyId);
//        System.out.println("Существует ли лобби? " + lobbies.containsKey(lobbyId));
//        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/finish_question",
//                new QuestionResultMessage("QUESTION_RESULTS", this.lobbies.get(lobbyId).finishQuestion())
//        );
        return this.lobbies.get(lobbyId).finishQuestion();

    }
}



package com.example.lobby_service.Repositories;

import com.example.lobby_service.Repositories.Entities.ChatMessage;
import com.example.lobby_service.Repositories.Entities.Lobby;
import com.example.lobby_service.Repositories.Entities.Player;
import com.example.lobby_service.Repositories.Entities.QuestionResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public void sendResults(String lobbyId) {
        System.out.println(lobbyId);
        System.out.println("Существует ли лобби? " + lobbies.containsKey(lobbyId));

        try {
            messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/finish_question",
                    new QuestionResultMessage("QUESTION_RESULTS", this.lobbies.get(lobbyId).finishQuestion())
            );
            System.out.println("Сообщение УСПЕШНО отправлено!");
        } catch (Exception e) {
            System.out.println("ОШИБКА ОТПРАВКИ: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<Player> getPlayers(String lobbyId) {
        List<Player> players = this.lobbies.get(lobbyId).getLobbyPlayers();
        Random rand = new Random();
        for (Player player : players ) {
            player.setX(rand.nextInt((600 - 200) + 1) + 200);
            player.setY(rand.nextInt((400 - 200) + 1) + 200);
        }
        return players;
    }
    public void upgradePlayerStats(String lobbyId, String username, Map<String, Float> upgradedStats) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) throw new IllegalArgumentException("Lobby not found");

        Player player = lobbies.get(lobbyId).FindPlayer(username);
        if (player == null) throw new IllegalArgumentException("Player not found");

        upgradedStats.forEach((statName, points) -> {
            if (points == null || points <= 0) return;
            double modificator = 1.5;
            switch (statName) {
                case "health" -> player.setHealth(player.getHealth() * modificator);
                case "move_speed" -> player.setMove_speed(player.getMove_speed() * modificator);
                case "attack_speed" -> player.setAttack_speed(player.getAttack_speed() * modificator);
                case "melee_power" -> player.setMelee_power(player.getMelee_power() * modificator);
                case "melee_range" -> player.setMelee_range(player.getMelee_range() * modificator);
                case "knockback_power" -> player.setKnockback_power(player.getKnockback_power() * modificator);
                case "knockback_friction" -> player.setKnockback_friction(player.getKnockback_friction() / modificator);
                default -> System.out.println("Неизвестный стат");
            }
        });
    }
}



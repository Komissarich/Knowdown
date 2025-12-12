package com.example.lobby_service.Repositories.Entities;

import java.util.*;

public class Lobby {

    private List<Player> lobbyPlayers = new ArrayList<Player>();
    private final Set<String> playerNames = new HashSet<>();
    List<ChatMessage> lobbyMessages = new ArrayList<ChatMessage>();

    public List<AnswerRequest> answers = new ArrayList<AnswerRequest>();
    private String lobbyId;
    private String name;
    private int maxPlayersCount;
    private boolean isPrivate;
    private Player creator;

    public Lobby(boolean isPrivate, int maxPlayersCount, String lobbyId, String name, String name_of_creator) {
        this.isPrivate = isPrivate;
        this.maxPlayersCount = maxPlayersCount;
        this.lobbyId = lobbyId;
        this.creator = new Player();
        this.creator.setUsername(name_of_creator);
        this.name= name;
    }

    public void AddAnswer(String name, Long timestamp) {
        answers.add(new AnswerRequest(name, this.lobbyId, timestamp));
    }

    public Map<String, Integer> finishQuestion() {
        List<AnswerRequest> answers_sorted = answers.stream().sorted(Comparator.comparingLong(AnswerRequest::getTimestamp)).toList();
        Map<String, Integer> points = new HashMap<>();
        for (int i = 0; i < answers_sorted.size(); i++) {
            String name = answers_sorted.get(i).getName();
            int score = switch (i) {
                case 0 -> 4;
                case 1 -> 3;
                case 2 -> 2;
                default -> 1;
            };
            points.put(name, score);
        }
        System.out.println("points:");
        System.out.println(points);
        this.clearAnswers();
        return points;
    }
    public void AddPlayer(Player player) {
        if (playerNames.add(player.getUsername())) {
            lobbyPlayers.add(player);
        }
    }

    public void RemovePlayer(Player player) {

        this.lobbyPlayers.remove (player);
    }
    public Player FindPlayer(String username) {
        return this.lobbyPlayers.stream()
                .filter(player_ -> "James".equals(player_.getUsername()))
                .findAny()
                .orElse(null);
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
    public void clearAnswers() {
        this.answers.clear();
    }
}

package com.example.lobby_service.Controllers;

import com.example.lobby_service.Repositories.Entities.ChatMessage;
import com.example.lobby_service.Repositories.Entities.LobbyRequest;
import com.example.lobby_service.Repositories.Entities.LobbyResponse;
import com.example.lobby_service.Repositories.Entities.Player;
import com.example.lobby_service.Repositories.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
public class LobbyController {
    private final LobbyService lobbyService = new LobbyService();

    @MessageMapping("/lobby/{lobbyId}/send_message")
    @SendTo("/topic/lobby/{lobbyId}/messages")
    public String processChatMessages(@DestinationVariable String lobbyId, String message) throws JsonProcessingException {
        System.out.println(message);
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
        lobbyService.AddMessage(lobbyId, chatMessage);
        return message;
    }

    @MessageMapping("/lobby/{lobbyId}/join")
    @SendTo("/topic/lobby/{lobbyId}/player_list")
    public List<Player> processLobbyList(@DestinationVariable String lobbyId, String message) throws JsonProcessingException {
        System.out.println(message);
        ObjectMapper objectMapper = new ObjectMapper();
        Player player = objectMapper.readValue(message, Player.class);
        lobbyService.AddPlayerInLobby(lobbyId, player);
        System.out.println("giving "+ lobbyService.ListPlayers(lobbyId));
        return lobbyService.ListPlayers(lobbyId);
    }

    @PostMapping("/game/lobby/create")
    public ResponseEntity<LobbyResponse> createLobby(@RequestBody LobbyRequest request) {
        System.out.println("creating lobby");
        String lobbyId = lobbyService.CreateLobby(
                request.isPrivate(),
                request.getMaxPlayers(),
                request.getName(),
                request.getCreator()
        );
        return ResponseEntity.ok(new LobbyResponse(lobbyId));
    }

}

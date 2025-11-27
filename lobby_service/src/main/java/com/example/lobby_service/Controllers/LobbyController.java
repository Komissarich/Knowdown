package com.example.lobby_service.Controllers;

import com.example.lobby_service.Repositories.CountDownService;
import com.example.lobby_service.Repositories.Entities.ChatMessage;
import com.example.lobby_service.Repositories.Entities.isCreatorRequest;
import com.example.lobby_service.Repositories.Entities.LobbyRequest;
import com.example.lobby_service.Repositories.Entities.LobbyResponse;
import com.example.lobby_service.Repositories.Entities.Player;
import com.example.lobby_service.Repositories.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lobby")
@CrossOrigin(origins = "http://localhost:8080", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
public class LobbyController {
    private final LobbyService lobbyService = new LobbyService();

    @Autowired
    private CountDownService quizCountdownService;
    @MessageMapping("/{lobbyId}/send_message")
    @SendTo("/topic/lobby/{lobbyId}/messages")
    public String processChatMessages(@DestinationVariable String lobbyId, String message) throws JsonProcessingException {
        System.out.println(message);
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
        lobbyService.AddMessage(lobbyId, chatMessage);
        return message;
    }

    @MessageMapping("/{lobbyId}/join")
    @SendTo("/topic/lobby/{lobbyId}/player_list")
    public List<Player> processLobbyList(@DestinationVariable String lobbyId, String message) throws JsonProcessingException {
        System.out.println(message);
        ObjectMapper objectMapper = new ObjectMapper();
        Player player = objectMapper.readValue(message, Player.class);
       
        lobbyService.AddPlayerInLobby(lobbyId, player);
        System.out.println("giving "+ lobbyService.ListPlayers(lobbyId));
        return lobbyService.ListPlayers(lobbyId);
    }

    @PostMapping("/isCreator")
    public boolean isAuthor(@RequestBody isCreatorRequest request) {
        System.out.println("check if creator");

        System.out.println(lobbyService.checkCreator(request.getLobby_name(), request.getUsername()));
        return lobbyService.checkCreator(request.getLobby_name(), request.getUsername());
    }

    @PostMapping("/start")
    public ResponseEntity<?> startQuiz(@RequestParam String lobby_name,
                                       @RequestParam String username) {

        System.out.println("starting the countdown");
        System.out.println(lobby_name);
        System.out.println(username);
        System.out.println(lobbyService.checkCreator(lobby_name, username));
        if (!lobbyService.checkCreator(lobby_name, username)) {
            return ResponseEntity.status(403).body("Только создатель может начать");
        }
        quizCountdownService.startCountdown(lobby_name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
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

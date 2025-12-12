package com.example.lobby_service.Controllers;

import com.example.lobby_service.Repositories.CountDownService;
import com.example.lobby_service.Repositories.Entities.*;
import com.example.lobby_service.Repositories.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lobby")
@CrossOrigin(origins = "http://localhost:8080", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
public class LobbyController {
    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private CountDownService quizCountdownService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


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
        System.out.println("giving " + lobbyService.ListPlayers(lobbyId));
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

    @PostMapping("/answer")
    public String answer(@RequestBody AnswerRequest request) {
        System.out.println("handling answer");
        System.out.println(request.getName() + request.getLobbyId() + request.getTimestamp());
        lobbyService.AddAnswer(request.getName(), request.getLobbyId(), request.getTimestamp());
        return "added answer";
    }

    @PostMapping("/finish")
//    @SendTo("/topic/lobby/{lobbyId}/finish_question")
    public String finish(@RequestParam String lobbyId) {
        System.out.println("finishing answer");
      lobbyService.sendResults(lobbyId);
        return "sent results";
    }

    @PostMapping("/{lobbyId}/get_questions")
    public String  getQuestions(@RequestBody QuestionRequest request, @RequestParam String lobbyId) throws JsonProcessingException {

        ApiGetQuestions apiGetQuestions = new ApiGetQuestions();
        apiGetQuestions.setAmount(request.getAmount());
        System.out.println(request.getAmount());
        List<String> types = new ArrayList<String>();
        apiGetQuestions.setTypes(List.of("TRUE_FALSE", "MULTIPLE_CHOICE"));
        apiGetQuestions.setCategories(request.getCategories());
        apiGetQuestions.setDifficulties(List.of("EASY", "MEDIUM"));
        RestClient restClient = RestClient.create();
        QuestionResponse response = restClient.post()
                .uri(UriComponentsBuilder.fromHttpUrl("http://question-service:8083/api/questions/give_questions")
                        .build()
                        .toUri())
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiGetQuestions)
                .retrieve()
                .body(QuestionResponse.class);
        System.out.println("try to get questions");
        System.out.println(apiGetQuestions.getAmount());
        System.out.println("response");
        System.out.println(response);
        messagingTemplate.convertAndSend(
                "/topic/lobby/" + lobbyId + "/get_questions",
                new QuestMessage(response)
        );
        return "almost";
    }

    @PostMapping("/upgrade")
    public String upgradeStats(@RequestBody UpgradeRequest request) {
        System.out.println("going to upgrades");
        System.out.println(request);
//        lobbyService.upgradePlayerStats(
//                request.username(),
//                request.lobbyId(),
//                request.upgradedStats()
//        );
        return "upgraded";
    }


    @MessageMapping("/arena/{arena_id}/move")
    public void playerMove(@DestinationVariable String arena_id,
                           @RequestBody PlayerPosition position) {
//        System.out.println("moving");
//        System.out.println(position);

        messagingTemplate.convertAndSend(
                "/topic/arena/" + arena_id + "/positions",
                position
        );
    }

}

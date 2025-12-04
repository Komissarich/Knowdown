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
    @SendTo("/topic/lobby/{lobbyId}/finish_question")
    public Map<String, Integer> finish(@RequestParam String lobbyId) {
        System.out.println("finishing answer");
//        lobbyService.sendResults(lobbyId);
        return lobbyService.sendResults(lobbyId);
    }

    @PostMapping("/{lobbyId}/get_questions")
    public String  getQuestions(@RequestBody QuestionRequest request, @RequestParam String lobbyId) throws JsonProcessingException {
        RestClient restClient = RestClient.create();
        QuestionResponse response = restClient.post()
                .uri("http://question_service:8082/api/questions/get_questions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(QuestionResponse.class);
    }
        System.out.println("try to get questions");
        System.out.println(request);

    ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(lobbyId);
    String s = """
    { "quiz_questions":
    [
    {
        "type": "multiple",
            "difficulty": "medium",
            "category": "Entertainment: Music",
            "question": "Who had hits in the 70s with the songs &quot;Lonely Boy&quot; and &quot;Never Let Her Slip Away&quot;?",
            "correct_answer": "Andrew Gold",
            "incorrect_answers": [
        "Elton John",
                "Leo Sayer",
                "Barry White "
      ]
    },
    {
        "type": "multiple",
            "difficulty": "hard",
            "category": "Science &amp; Nature",
            "question": "What is the scientific name of the knee cap?",
            "correct_answer": "Patella",
            "incorrect_answers": [
        "Femur",
                "Foramen Magnum",
                "Scapula"
      ]
    },
    {
        "type": "multiple",
            "difficulty": "hard",
            "category": "Entertainment: Music",
            "question": "Which M83 album is the song &quot;Midnight City&quot; featured in?",
            "correct_answer": "Hurry Up, We&#039;re Dreaming",
            "incorrect_answers": [
        "Saturdays = Youth",
                "Before the Dawn Heals Us",
                "Junk"
      ]
    },
    {
        "type": "multiple",
            "difficulty": "medium",
            "category": "Entertainment: Video Games",
            "question": "How many classes are there in Team Fortress 2?",
            "correct_answer": "9",
            "incorrect_answers": [
        "10",
                "8",
                "7"
      ]
    },
    {
        "type": "boolean",
            "difficulty": "medium",
            "category": "General Knowledge",
            "question": "&quot;Buffalo buffalo Buffalo buffalo buffalo buffalo Buffalo buffalo.&quot; is a grammatically correct sentence.",
            "correct_answer": "True",
            "incorrect_answers": [
        "False"
      ]
    },
    {
        "type": "multiple",
            "difficulty": "medium",
            "category": "Science: Computers",
            "question": "What does the term GPU stand for?",
            "correct_answer": "Graphics Processing Unit",
            "incorrect_answers": [
        "Gaming Processor Unit",
                "Graphite Producing Unit",
                "Graphical Proprietary Unit"
      ]
    },
    {
        "type": "boolean",
            "difficulty": "easy",
            "category": "General Knowledge",
            "question": "In architecture, a &quot;pecklesteiner&quot; is a set of doors that overlap each other when closed, and can be locked through a single keyhole.",
            "correct_answer": "False",
            "incorrect_answers": [
        "True"
      ]
    },
    {
        "type": "multiple",
            "difficulty": "hard",
            "category": "Sports",
            "question": "Which English football team is nicknamed &#039;The Tigers&#039;?",
            "correct_answer": "Hull City",
            "incorrect_answers": [
        "Cardiff City",
                "Bristol City",
                "Manchester City"
      ]
    },
    {
        "type": "multiple",
            "difficulty": "easy",
            "category": "Entertainment: Video Games",
            "question": "What company developed the Xbox line of video game consoles?",
            "correct_answer": "Microsoft",
            "incorrect_answers": [
        "Sony",
                "Toshiba",
                "IBM"
      ]
    },
    {
        "type": "multiple",
            "difficulty": "easy",
            "category": "General Knowledge",
            "question": "Which one of the following rhythm games was made by Harmonix?",
            "correct_answer": "Rock Band",
            "incorrect_answers": [
        "Meat Beat Mania",
                "Guitar Hero Live",
                "Dance Dance Revolution"
      ]
    }
  ]
  }""";
        QuestionResponse response = objectMapper.readValue(s, QuestionResponse.class);
        messagingTemplate.convertAndSend(
                "/topic/lobby/" + lobbyId + "/get_questions",
                new QuestMessage(response)
        );
        return "almost";
    }





}

package knowdown.user_service.controller;

import knowdown.user_service.domain.Friendship;
import knowdown.user_service.dto.FriendRequest;
import knowdown.user_service.dto.FriendResponse;
import knowdown.user_service.service.FriendshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendshipController {

    private static final Logger log = LoggerFactory.getLogger(FriendshipController.class);

    @Autowired
    private FriendshipService friendshipService;

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException();
    }

    //добавить в друзья
    @PostMapping("/add")
    public ResponseEntity<?> addFriend(@RequestBody FriendRequest request) {
        try {
            String currentUsername = getCurrentUsername();

            log.info("Пользователь {} пытается добавить в друзья: {}", currentUsername, request.getFriendUsername());

            boolean success = friendshipService.sendFriendRequest(currentUsername, request.getFriendUsername());

            if (success) {
                log.info("Запрос в друзья отправлен от {} к {}", currentUsername, request.getFriendUsername());
                return ResponseEntity.ok().build();
            } else {
                log.warn("Не удалось отправить запрос в друзья от {} к {}", currentUsername, request.getFriendUsername());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке запроса в друзья: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //получить список друзей
    @GetMapping
    public ResponseEntity<?> getFriends() {
        try {
            String currentUsername = getCurrentUsername();

            log.info("Получение списка друзей для пользователя: {}", currentUsername);

            List<Friendship> friendships = friendshipService.getFriends(currentUsername);
            List<FriendResponse> friendResponses = new ArrayList<>();

            for (Friendship friendship : friendships) {
                //определяем кто друг - user или friend
                String friendUsername = friendship.getUser().getUsername().equals(currentUsername)
                        ? friendship.getFriend().getUsername()
                        : friendship.getUser().getUsername();

                FriendResponse response = new FriendResponse(
                        friendUsername,
                        friendship.getStatus().toString()
                );
                friendResponses.add(response);
            }

            log.info("Найдено {} друзей для пользователя {}", friendResponses.size(), currentUsername);
            return ResponseEntity.ok(friendResponses);
        } catch (Exception e) {
            log.error("Ошибка при получении списка друзей: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //получить входящие запросы в друзья
    @GetMapping("/requests")
    public ResponseEntity<?> getFriendRequests() {
        try {
            String currentUsername = getCurrentUsername();

            log.info("Получение входящих запросов в друзья для пользователя: {}", currentUsername);

            List<Friendship> pendingRequests = friendshipService.getPendingRequests(currentUsername);
            List<FriendResponse> requests = new ArrayList<>();

            for (Friendship friendship : pendingRequests) {
                FriendResponse response = new FriendResponse(
                        friendship.getUser().getUsername(),
                        friendship.getStatus().toString()
                );
                requests.add(response);
            }

            log.info("Найдено {} входящих запросов для пользователя {}", requests.size(), currentUsername);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            log.error("Ошибка при получении запросов в друзья: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //принять запрос в друзья
    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendRequest request) {
        try {
            String currentUsername = getCurrentUsername();

            log.info("Пользователь {} принимает запрос в друзья от {}", currentUsername, request.getFriendUsername());

            boolean success = friendshipService.acceptFriendRequest(currentUsername, request.getFriendUsername());

            if (success) {
                log.info("Запрос в друзья принят пользователем {} от {}", currentUsername, request.getFriendUsername());
                return ResponseEntity.ok().build();
            } else {
                log.warn("Не удалось принять запрос в друзья пользователем {} от {}", currentUsername, request.getFriendUsername());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Ошибка при принятии запроса в друзья: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //отклонить запрос в друзья
    @PostMapping("/reject")
    public ResponseEntity<?> rejectFriendRequest(@RequestBody FriendRequest request) {
        try {
            String currentUsername = getCurrentUsername();

            log.info("Пользователь {} отклоняет запрос в друзья от {}", currentUsername, request.getFriendUsername());

            boolean success = friendshipService.rejectFriendRequest(currentUsername, request.getFriendUsername());

            if (success) {
                log.info("Запрос в друзья отклонен пользователем {} от {}", currentUsername, request.getFriendUsername());
                return ResponseEntity.ok().build();
            } else {
                log.warn("Не удалось отклонить запрос в друзья пользователем {} от {}", currentUsername, request.getFriendUsername());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Ошибка при отклонении запроса в друзья: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    //удалить из друзей
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFriend(@RequestBody FriendRequest request) {
        try {
            String currentUsername = getCurrentUsername();

            log.info("Пользователь {} удаляет из друзей: {}", currentUsername, request.getFriendUsername());

            boolean success = friendshipService.removeFriend(currentUsername, request.getFriendUsername());

            if (success) {
                log.info("Пользователь {} удален из друзей пользователем {}", request.getFriendUsername(), currentUsername);
                return ResponseEntity.ok().build();
            } else {
                log.warn("Не удалось удалить из друзей пользователем {} пользователя {}", currentUsername, request.getFriendUsername());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении из друзей: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
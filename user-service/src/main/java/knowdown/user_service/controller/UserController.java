package knowdown.user_service.controller;

import knowdown.user_service.dto.LoginRequest;
import knowdown.user_service.dto.RegisterRequest;
import knowdown.user_service.dto.AuthResponse;
import knowdown.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("Вызов метода login для пользователя: {}", request.getUsername());

        //проверяем, что username и email не пустые
        if ((request.getUsername() == null || request.getUsername().trim().isEmpty()) &&
                (request.getEmail() == null || request.getEmail().trim().isEmpty())) {
            log.warn("Попытка входа без имени пользователя и email");
            return ResponseEntity.badRequest().body(new AuthResponse(null, false));
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.warn("Попытка входа с пустым паролем");
            return ResponseEntity.badRequest().body(new AuthResponse(null, false));
        }

        try {
            String usernameOrEmail = request.getUsername() != null ?
                    request.getUsername().trim() :
                    request.getEmail().trim();
            String token = userService.authenticateAndGetToken(usernameOrEmail, request.getPassword());

            if (token != null) {
                log.info("Пользователь {} успешно аутентифицирован", usernameOrEmail);
                return ResponseEntity.ok(new AuthResponse(token, true));
            } else {
                log.warn("Ошибка аутентификации для: {}", usernameOrEmail);
                return ResponseEntity.badRequest().body(new AuthResponse(null, false));
            }
        } catch (Exception e) {
            log.error("Ошибка при входе: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new AuthResponse(null, false));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("Вызов метода register для пользователя: {}, email: {}",
                request.getUsername(), request.getEmail());

        //входные данные
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.warn("Попытка регистрации с пустым именем пользователя");
            return ResponseEntity.badRequest().body(new AuthResponse(null, false));
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.warn("Попытка регистрации с пустым email");
            return ResponseEntity.badRequest().body(new AuthResponse(null, false));
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.warn("Попытка регистрации с пустым паролем");
            return ResponseEntity.badRequest().body(new AuthResponse(null, false));
        }

        //минимальную длину пароля
        if (request.getPassword().length() < 6) {
            log.warn("Попытка регистрации с слишком коротким паролем");
            return ResponseEntity.badRequest().body(new AuthResponse(null, false));
        }

        //валидация email
        if (!isValidEmail(request.getEmail())) {
            log.warn("Некорректный email: {}", request.getEmail());
            return ResponseEntity.badRequest().body(new AuthResponse(null, false));
        }

        try {
            boolean isRegistered = userService.registerUser(
                    request.getUsername().trim(),
                    request.getEmail().trim(),
                    request.getPassword()
            );

            if (isRegistered) {
                log.info("Пользователь {} успешно зарегистрирован", request.getUsername());
                //входим после регистрации
                String token = userService.authenticateAndGetToken(request.getUsername(), request.getPassword());
                return ResponseEntity.ok(new AuthResponse(token, true));
            } else {
                log.warn("Ошибка регистрации - имя пользователя или email уже заняты");
                return ResponseEntity.badRequest().body(new AuthResponse(null, false));
            }
        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(new AuthResponse(null, false));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Работает ли сервис");
        return ResponseEntity.ok("сервис запущен");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
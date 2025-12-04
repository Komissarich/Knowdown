package knowdown.user_service.controller;

import knowdown.user_service.dto.LoginRequest;
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

        //проверяем, что username и password не пустые
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.warn("Попытка входа с пустым именем пользователя");
            AuthResponse response = new AuthResponse(null, "Имя пользователя не может быть пустым", false);
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.warn("Попытка входа с пустым паролем для пользователя: {}", request.getUsername());
            AuthResponse response = new AuthResponse(null, "Пароль не может быть пустым", false);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            //путентифицируем пользователя и получаем JWT токен
            String token = userService.authenticateAndGetToken(request.getUsername(), request.getPassword());

            if (token != null) {
                log.info("Пользователь {} успешно аутентифицирован", request.getUsername());
                AuthResponse response = new AuthResponse(token, "Вход выполнен успешно", true);
                return ResponseEntity.ok(response);
            } else {
                log.warn("Ошибка аутентификации для пользователя: {}", request.getUsername());
                AuthResponse response = new AuthResponse(null, "Неверное имя пользователя или пароль", false);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Ошибка при входе для пользователя {}: {}", request.getUsername(), e.getMessage());
            AuthResponse response = new AuthResponse(null, "Внутренняя ошибка сервера", false);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest request) {
        log.info("Вызов метода register для пользователя: {}", request.getUsername());

        //проверяем входные данные
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.warn("Попытка регистрации с пустым именем пользователя");
            AuthResponse response = new AuthResponse(null, "Имя пользователя не может быть пустым", false);
            return ResponseEntity.badRequest().body(response);
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.warn("Попытка регистрации с пустым паролем для пользователя: {}", request.getUsername());
            AuthResponse response = new AuthResponse(null, "Пароль не может быть пустым", false);
            return ResponseEntity.badRequest().body(response);
        }

        //проверяем минимальную длину пароля
        if (request.getPassword().length() < 6) {
            log.warn("Попытка регистрации с слишком коротким паролем для пользователя: {}", request.getUsername());
            AuthResponse response = new AuthResponse(null, "Пароль должен содержать не менее 6 символов", false);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            boolean isRegistered = userService.registerUser(request.getUsername(), request.getPassword());

            if (isRegistered) {
                log.info("Пользователь {} успешно зарегистрирован", request.getUsername());
                String token = userService.authenticateAndGetToken(request.getUsername(), request.getPassword());
                AuthResponse response = new AuthResponse(token, "Registration successful", true);
                return ResponseEntity.ok(response);
            } else {
                log.warn("Ошибка регистрации - имя пользователя уже занято: {}", request.getUsername());
                AuthResponse response = new AuthResponse(null, "Имя пользователя уже занято", false);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя {}: {}", request.getUsername(), e.getMessage());
            AuthResponse response = new AuthResponse(null, "Внутренняя ошибка сервера при регистрации", false);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Работает ли сервис");
        return ResponseEntity.ok("сервис запущен");
    }
}
package knowdown.user_service.controller;


import knowdown.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {
        boolean isAuthenticated = userService.loginUser(username, password);
        log.info("login method called");
        if (isAuthenticated) {
            model.addAttribute("message", "Вход выполнен успешно, " + username + "!");
            return "home";
        } else {
            model.addAttribute("error", "Неверное имя пользователя или пароль.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           Model model) {
        boolean isRegistered = userService.registerUser(username, password);

        if (isRegistered) {
            model.addAttribute("message", "Регистрация прошла успешно! Теперь вы можете войти.");
            return "login";
        } else {
            model.addAttribute("error", "Имя пользователя уже занято.");
            return "register";
        }
    }
}
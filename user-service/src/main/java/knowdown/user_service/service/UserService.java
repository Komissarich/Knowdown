package knowdown.user_service.service;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    boolean registerUser(String username, String email, String password);
    String authenticateAndGetToken(String usernameOrEmail, String password);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

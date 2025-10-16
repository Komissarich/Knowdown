package knowdown.user_service.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    boolean registerUser(String username, String password);
//    boolean loginUser(String username, String password);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    String authenticateAndGetToken(String username, String password);
}

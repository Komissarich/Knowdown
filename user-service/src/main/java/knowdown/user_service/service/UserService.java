package knowdown.user_service.service;

public interface UserService {
    boolean registerUser(String username, String password);
    boolean loginUser(String username, String password);
}

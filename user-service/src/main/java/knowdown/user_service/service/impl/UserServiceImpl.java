package knowdown.user_service.service.impl;

import knowdown.user_service.domain.User;
import knowdown.user_service.repository.UserRepository;
import knowdown.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; //spring security

    @Override
    public boolean registerUser(String username, String password) {
        //существует ли пользователь
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return false; //уже существует
        }

        User newUser = new User(username, passwordEncoder.encode(password));
        userRepository.save(newUser);
        return true;
    }

    @Override
    public boolean loginUser(String username, String password) {
        //поиск пользователя в базе данных
        Optional<User> user = userRepository.findByUsername(username);
        //проверяем совпадает ли пароль с его хэшем
        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
    }
}
package knowdown.user_service.service.impl;

import knowdown.user_service.domain.User;
import knowdown.user_service.repository.UserRepository;
import knowdown.user_service.service.JwtService;
import knowdown.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

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

//    @Override
//    public boolean loginUser(String username, String password) {
//        //поиск пользователя в базе данных
//        Optional<User> user = userRepository.findByUsername(username);
//        //проверяем совпадает ли пароль с его хэшем
//        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("пользователь не найден" + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }

    @Override
    public String authenticateAndGetToken(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            UserDetails userDetails = loadUserByUsername(username);
            return jwtService.generateToken(userDetails);
        }
        return null;
    }
}
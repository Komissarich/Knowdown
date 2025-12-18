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
    public boolean registerUser(String username, String email, String password) {
        //существует ли пользователь с таким username или email
        if (existsByUsername(username)) {
            return false; //username уже занят
        }

        if (existsByEmail(email)) {
            return false; //email уже занят
        }

        User newUser = new User(username, email, passwordEncoder.encode(password));
        userRepository.save(newUser);
        return true;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //найти по username, затем по email
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username)));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }

    @Override
    public String authenticateAndGetToken(String usernameOrEmail, String password) {
        //найти пользователя по username или email
        Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            UserDetails userDetails = loadUserByUsername(user.get().getUsername());
            return jwtService.generateToken(userDetails);
        }
        return null;
    }
}
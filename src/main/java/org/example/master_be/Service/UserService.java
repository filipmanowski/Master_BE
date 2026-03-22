package org.example.master_be.Service;

import org.example.master_be.Model.User;
import org.example.master_be.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public record LoginResult(boolean success, User user) {}
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // REJESTRACJA
    public User register(String email, String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(email, hashedPassword);
        return userRepository.save(user);
    }

    // LOGOWANIE
    public LoginResult login(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
                    return passwordMatch
                            ? new LoginResult(true, user)
                            : new LoginResult(false, null);
                })
                .orElse(new LoginResult(false, null));
    }
}
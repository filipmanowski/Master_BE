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

    public enum LoginStatus {
        SUCCESS,
        INVALID_CREDENTIALS,
        PROFILE_INCOMPLETE
    }

    public record LoginResult(LoginStatus status, User user) {
    }

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
                    if (!passwordMatch) {
                        return new LoginResult(LoginStatus.INVALID_CREDENTIALS, null);
                    }
                    if (!Boolean.TRUE.equals(user.getEnabled())) {
                        return new LoginResult(LoginStatus.PROFILE_INCOMPLETE, user);
                    }
                    return new LoginResult(LoginStatus.SUCCESS, user);
                })
                .orElse(new LoginResult(LoginStatus.INVALID_CREDENTIALS, null));
    }
}
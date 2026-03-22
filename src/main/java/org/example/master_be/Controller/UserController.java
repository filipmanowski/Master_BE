package org.example.master_be.Controller;


import org.example.master_be.DTO.LoginRequest;
import org.example.master_be.DTO.RegisterRequest;
import org.example.master_be.DTO.UserResponse;
import org.example.master_be.Model.User;
import org.example.master_be.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // REJESTRACJA
    @PostMapping("/auth/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(
                request.getEmail(),
                request.getPassword()
        );
    }

    // LOGOWANIE
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        UserService.LoginResult result = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        if (!result.success()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Wrong credentials"));
        }

        User user = result.user();

        UserResponse response = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getEnabled(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}
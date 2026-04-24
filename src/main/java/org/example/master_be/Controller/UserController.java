package org.example.master_be.Controller;


import org.example.master_be.Config.JwtService;
import org.example.master_be.DTO.AuthResponse;
import org.example.master_be.DTO.LoginRequest;
import org.example.master_be.DTO.RegisterRequest;
import org.example.master_be.Model.User;
import org.example.master_be.Service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public UserController(UserService userService,
                          JwtService jwtService,
                          UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
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

        if (result.status() == UserService.LoginStatus.INVALID_CREDENTIALS) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Wrong credentials"));
        }

        User user = result.user();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        boolean needsProfileCompletion = result.status() == UserService.LoginStatus.PROFILE_INCOMPLETE;

        AuthResponse response = new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getEnabled(),
                user.getRole(),
                needsProfileCompletion
        );

        return ResponseEntity.ok(response);
    }
}
package org.example.master_be.Controller;


import org.example.master_be.DTO.LoginRequest;
import org.example.master_be.DTO.RegisterRequest;
import org.example.master_be.Model.User;
import org.example.master_be.Service.UserService;
import org.springframework.web.bind.annotation.*;

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
    public String login(@RequestBody LoginRequest request) {

        boolean success = userService.login(request.getEmail(),
                request.getPassword()
        );

        if (success) {
            return "Login OK";
        } else {
            return "Wrong password";
        }
    }
}
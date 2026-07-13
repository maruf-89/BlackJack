package blackjack.controller;


import blackjack.dto.RegisterRequest;
import blackjack.entity.User;
import blackjack.service.UserService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;

    }

    @GetMapping
    public List<User> getUsers() {

        return userService.getAllUsers();

    }

    @PostMapping("/register")
    public User register(
            @RequestBody RegisterRequest request
    ) {

        return userService.register(request);

    }

}
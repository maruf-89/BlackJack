package blackjack.controller;


import blackjack.dto.RegisterRequest;
import blackjack.entity.User;
import blackjack.service.UserService;
import java.security.Principal;
import blackjack.dto.ChangePasswordRequest;

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

    @PutMapping("/password")
    public void changePassword(
            Principal principal,
            @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(
                principal.getName(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );
    }

    @GetMapping("/me")
    public User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName());
    }

    @PostMapping("/register")
    public User register(
            @RequestBody RegisterRequest request
    ) {

        return userService.register(request);

    }

}


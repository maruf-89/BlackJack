package blackjack.controller;

import blackjack.dto.ChangePasswordRequest;
import blackjack.dto.RegisterRequest;
import blackjack.dto.TransactionResponse;
import blackjack.entity.User;
import blackjack.service.UserService;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @GetMapping("/me")
    public User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName());
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> getTransactions(Principal principal) {
        return userService.getTransactions(principal.getName());
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request);
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
}
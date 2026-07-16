package blackjack.controller;

import blackjack.dto.BalanceUpdateRequest;
import blackjack.dto.UserStatusRequest;
import blackjack.entity.User;
import blackjack.service.UserService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/users/{id}/admin")
    public User makeAdmin(@PathVariable Long id) {
        return userService.makeAdmin(id);
    }

    @PutMapping("/users/{id}/balance")
    public User updateBalance(
            @PathVariable Long id,
            @RequestBody BalanceUpdateRequest request
    ) {
        return userService.updateBalance(id, request.getBalance());
    }

    @PutMapping("/users/{id}/status")
    public User setUserStatus(
            Principal principal,
            @PathVariable Long id,
            @RequestBody UserStatusRequest request
    ) {
        return userService.setEnabled(principal.getName(), id, request.isEnabled());
    }
}
package blackjack.controller;

import blackjack.dto.HighscoreEntry;
import blackjack.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/highscores")
public class HighscoreController {

    private final UserService userService;

    public HighscoreController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<HighscoreEntry> getHighscores() {
        return userService.getHighscores();
    }
}
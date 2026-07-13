package blackjack.controller;

import blackjack.game.GameState;
import blackjack.service.BlackjackService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blackjack")
public class BlackjackController {

    private final BlackjackService blackjackService;

    public BlackjackController(
            BlackjackService blackjackService
    ) {

        this.blackjackService = blackjackService;

    }

    @GetMapping("/start")
    public GameState startGame() {

        return blackjackService.startGame();

    }

}
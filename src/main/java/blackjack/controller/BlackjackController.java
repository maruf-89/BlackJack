package blackjack.controller;

import blackjack.game.GameState;
import blackjack.service.BlackjackService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String startGame() {

        return blackjackService.startGame();

    }

    @GetMapping("/{gameId}")
    public GameState getGame(
            @PathVariable String gameId
    ) {

        return blackjackService.getGame(gameId);

    }

    @GetMapping("/{gameId}/hit")
    public GameState hit(
            @PathVariable String gameId
    ) {

        return blackjackService.hit(gameId);

    }

}
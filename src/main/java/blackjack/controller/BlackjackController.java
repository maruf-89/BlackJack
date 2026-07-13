package blackjack.controller;

import blackjack.game.GameResult;
import blackjack.game.GameState;
import blackjack.service.BlackjackService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import blackjack.dto.StartGameRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/blackjack")
public class BlackjackController {

    private final BlackjackService blackjackService;

    public BlackjackController(
            BlackjackService blackjackService
    ) {

        this.blackjackService = blackjackService;

    }

    @PostMapping("/start")
    public String startGame(
            @RequestBody StartGameRequest request
    ) {

        return blackjackService.startGame(
                request.getBet()
        );

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

    @GetMapping("/{gameId}/stand")
    public GameResult stand(
            @PathVariable String gameId
    ) {

        return blackjackService.stand(gameId);

    }

}
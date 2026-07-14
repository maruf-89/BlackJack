package blackjack.controller;

import blackjack.dto.StartGameRequest;
import blackjack.game.GameState;
import blackjack.service.BlackjackService;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;


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
            Principal principal,
            @RequestBody StartGameRequest request
    ) {

        return blackjackService.startGame(
                principal.getName(),
                request.getBet()
        );

    }


    @PostMapping("/{gameId}/hit")
    public GameState hit(
            @PathVariable String gameId
    ) {

        return blackjackService.hit(gameId);

    }


    @PostMapping("/{gameId}/stand")
    public GameState stand(
            @PathVariable String gameId
    ) {

        return blackjackService.stand(gameId);

    }


    @GetMapping("/{gameId}")
    public GameState getGame(
            @PathVariable String gameId
    ) {

        return blackjackService.getGame(gameId);

    }

}
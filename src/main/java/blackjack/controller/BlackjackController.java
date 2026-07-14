package blackjack.controller;

import blackjack.dto.GameResponse;
import blackjack.dto.StartGameRequest;
import blackjack.dto.StartGameResponse;
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
    public StartGameResponse startGame(
            Principal principal,
            @RequestBody StartGameRequest request
    ) {

        String gameId =
                blackjackService.startGame(
                        principal.getName(),
                        request.getBet()
                );


        return new StartGameResponse(gameId);

    }


    @PostMapping("/{gameId}/hit")
    public GameResponse hit(
            @PathVariable String gameId
    ) {

        return blackjackService.hit(gameId);

    }


    @PostMapping("/{gameId}/stand")
    public GameResponse stand(
            @PathVariable String gameId
    ) {

        return blackjackService.stand(gameId);

    }


    @GetMapping("/{gameId}")
    public GameResponse getGame(
            @PathVariable String gameId
    ) {

        return blackjackService.getGame(gameId);

    }

}
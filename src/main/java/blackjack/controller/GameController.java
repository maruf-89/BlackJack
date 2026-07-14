package blackjack.controller;


import blackjack.dto.GameResponse;
import blackjack.service.BlackjackService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/game")
public class GameController {


    private final BlackjackService blackjackService;


    public GameController(
            BlackjackService blackjackService
    ){

        this.blackjackService = blackjackService;

    }

    @PostMapping("/start")
    @ResponseBody
    public String start(
            @RequestParam String username,
            @RequestParam BigDecimal bet
    ){


        return blackjackService.startGame(
                username,
                bet
        );

    }

    @GetMapping("/{id}")
    @ResponseBody
    public GameResponse getGame(
            @PathVariable String id
    ){


        return blackjackService.getGame(id);

    }

    @PostMapping("/{id}/hit")
    @ResponseBody
    public GameResponse hit(
            @PathVariable String id
    ){


        return blackjackService.hit(id);

    }

    @PostMapping("/{id}/stand")
    @ResponseBody
    public GameResponse stand(
            @PathVariable String id
    ){


        return blackjackService.stand(id);

    }


}
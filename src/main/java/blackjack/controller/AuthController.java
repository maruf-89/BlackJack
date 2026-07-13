package blackjack.controller;

import blackjack.dto.LoginRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class AuthController {


    private final AuthenticationManager authenticationManager;


    public AuthController(AuthenticationManager authenticationManager) {

        this.authenticationManager = authenticationManager;

    }


    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );


        return "Login successful";

    }

}
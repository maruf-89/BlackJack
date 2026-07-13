package blackjack.controller;

import blackjack.dto.LoginRequest;
import blackjack.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }


    @PostMapping("/login")
    public String login(
            @RequestBody LoginRequest request
    ) {


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );


        return jwtService.generateToken(
                request.getUsername()
        );

    }

}
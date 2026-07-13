package blackjack.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;


@Service
public class JwtService {


    private final String secret =
            "blackjack-secret-key-blackjack-secret-key-123456";


    private final long expiration =
            1000 * 60 * 60;


    private SecretKey getKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes()
        );

    }


    public String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getKey())
                .compact();

    }

}
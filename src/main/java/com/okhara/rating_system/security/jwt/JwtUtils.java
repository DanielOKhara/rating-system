package com.okhara.rating_system.security.jwt;


import com.okhara.rating_system.security.AppUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    private final SecretKey secretKey;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    public JwtUtils(@Value("${app.jwt.secret}") String jwtSecret){
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(AppUserDetails userDetails){
        return generateTokenFromUsername(userDetails.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tokenExpiration.toMillis()))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validate(String authToken){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex){
            log.error("Invalid signature or encryption issue: {}", ex.getMessage());
        } catch (MalformedJwtException ex){
            log.error("Invalid token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex){
            log.error("Token is expired: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex){
            log.error("Token is unsupported: {}", ex.getMessage());
        } catch (IllegalArgumentException ex){
            log.error("Claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}

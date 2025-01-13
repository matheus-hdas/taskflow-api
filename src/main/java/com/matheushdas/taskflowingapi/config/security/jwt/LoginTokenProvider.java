package com.matheushdas.taskflowingapi.config.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.matheushdas.taskflowingapi.dto.auth.LoginResponse;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginTokenProvider {
    @Value("${security.jwt.token.secret:secret}")
    private String secret;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000;

    private final UserDetailsService userDetailsService;
    private Algorithm algorithm;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public LoginResponse generateAuthorizationToken(String username, List<String> roles) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = LocalDateTime.now().plusSeconds(validityInMilliseconds / 1000);
        String accessToken = getAccessToken(username, roles, now, expiration);
        String refreshToken = getRefreshToken(username, roles, now);

        return new LoginResponse(
                username,
                true,
                now,
                expiration,
                accessToken,
                refreshToken
        );
    }

    private String getAccessToken(String username, List<String> roles, LocalDateTime now, LocalDateTime expiration) {
        String issuerUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath().toUriString();

        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(Instant.from(now))
                .withExpiresAt(Instant.from(expiration))
                .withSubject(username)
                .withIssuer(issuerUrl)
                .sign(algorithm)
                .strip();
    }

    private String getRefreshToken(String username, List<String> roles, LocalDateTime now) {
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(Instant.from(now))
                .withExpiresAt(Instant.from(now).plusSeconds((validityInMilliseconds * 3) / 1000))
                .withSubject(username)
                .sign(algorithm)
                .strip();
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decoded = decodeToken(token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(decoded.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodeToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if(bearerToken == null) return null;
        if(bearerToken.startsWith("Bearer ")) return bearerToken.substring("Bearer ".length());
        return null;
    }

    public boolean validadeToken(String token) {
        DecodedJWT decoded = decodeToken(token);
        try {
            if(decoded.getExpiresAt().before(new Date())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Expired or invalid token");
        }
    }
}

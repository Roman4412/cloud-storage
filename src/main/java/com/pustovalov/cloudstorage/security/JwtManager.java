package com.pustovalov.cloudstorage.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtManager {

    private final Duration lifetime;

    private final String issuer;

    private final SecretKey secretKey;

    public JwtManager(@Value("${jwt.lifetime}") Duration lifetime,
                      @Value("${jwt.issuer}") String issuer,
                      @Value("${jwt.secret}") String secret) {

        this.lifetime = lifetime;
        this.issuer = issuer;
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
    }

    public String generateToken(UserDetails userDetails) {
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());

        Map<String, Object> claims = new HashMap<>();
        String username = userDetails.getUsername();
        claims.put("username", username);
        log.debug("generating a token for userDetails {}", userDetails);
        return Jwts.builder()
                   .issuer(issuer)
                   .issuedAt(issuedDate)
                   .expiration(expiredDate)
                   .subject(username)
                   .claims(claims)
                   .signWith(secretKey)
                   .compact();
    }

    public String parseClaims(String token, String key) {
        log.debug("parsing {} from token {}", key, token);
        return Jwts.parser()
                   .verifyWith(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .get(key)
                   .toString();
    }

}
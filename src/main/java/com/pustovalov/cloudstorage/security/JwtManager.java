package com.pustovalov.cloudstorage.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtManager {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration lifetime;

    @Value("${jwt.issuer}")
    private String issuer;

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
                   .signWith(generateKey(secret))
                   .compact();
    }

    public String parseClaims(String token, String key) {
        log.debug("parsing {} from token {}", key, token);
        return Jwts.parser()
                   .verifyWith(generateKey(secret))
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .get(key)
                   .toString();
    }

    private SecretKey generateKey(String secret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
    }

}
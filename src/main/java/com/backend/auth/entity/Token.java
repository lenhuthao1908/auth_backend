package com.backend.auth.entity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

public class Token {
    @Getter
    private final String token;
    @Getter
    private static LocalDateTime issueAt;
    @Getter
    private static LocalDateTime expiredAt;

    public Token(String token, LocalDateTime issueAt, LocalDateTime expiredAt){
        this.token=token;
        this.issueAt = issueAt;
        this.expiredAt = expiredAt;
    }
    public static Token of(Long userId, Long validityInMinutes, String secretKey){
        var issueDate = Instant.now();
        //SecretKey keys = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
        var expiration = issueDate.plus(validityInMinutes, ChronoUnit.MINUTES);
        return new Token( Jwts.builder()
                .claim("user_id", userId)
                .setIssuedAt(Date.from(issueDate))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact(), issueAt, expiredAt);

    }

    public static Token of(String token){
        return new Token(token, issueAt, expiredAt);
    }

    public static Long from(String token, String secretKey){
        return ((Claims) Jwts.parserBuilder()
                .setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parse(token)
                .getBody())
                .get("user_id", Long.class);
    }



}

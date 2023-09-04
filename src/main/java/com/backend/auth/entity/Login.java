package com.backend.auth.entity;

import lombok.Getter;

public class Login {
    @Getter
    private final Jwt accessToken;
    @Getter
    private final Jwt refreshToken;

    private static final Long ACCESS_TOKEN_VALIDITY = 1L;
    private static final Long REFRESH_TOKEN_VALIDITY = 1440L;

    public Login(Jwt accessToken, Jwt refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    public static Login of(Long userId, String accessSecret, String refreshSecret, String role) {
        return new Login(
                Jwt.of(userId, ACCESS_TOKEN_VALIDITY, accessSecret, role),
                Jwt.of(userId, REFRESH_TOKEN_VALIDITY, refreshSecret, role)
        );
    }

    public static Login of(Long userId, String accessSerret, Jwt refreshToken, String role){
        return new Login(
                Jwt.of(userId, ACCESS_TOKEN_VALIDITY, accessSerret, role),
                refreshToken
        );
    }
}

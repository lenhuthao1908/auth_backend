package com.backend.auth.Repo;

import com.backend.auth.entity.Token;

import java.time.LocalDateTime;

public interface ITokenRepo {

    public Token addToken(String token, LocalDateTime issueAt, LocalDateTime expiration, Long id);
    public Long findByUserId(Long id);
    public int isUserId(Long id);

    public Token updateToken(String token, LocalDateTime issueAt, LocalDateTime expiration, Long id);
    public boolean removeToken(String token);

    public boolean isFindToken(String token);
}

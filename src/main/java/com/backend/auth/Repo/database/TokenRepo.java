package com.backend.auth.Repo.database;

import com.backend.auth.Repo.ITokenRepo;
import com.backend.auth.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenRepo implements ITokenRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public Token addToken(String token, LocalDateTime issueAt, LocalDateTime ExpirAt, Long id) {
        String SQL = "insert into [Token] values(?, ?, ?, ?)";
        int result = jdbcTemplate.update(SQL, new Object[]{
                token,
                issueAt,
                ExpirAt,
                id
        });
        if (result==1)
            return Token.of(token);
        return null;
    }

    @Override
    public Long findByUserId(Long id) {
        String SQL = "select * from [Token] where user = ?";
        return (long) jdbcTemplate.update(SQL, new Object[]{id});
    }

    @Override
    public int isUserId(Long id) {
        String SQL = "select * from [Token] where user = ?";
        return jdbcTemplate.update(SQL, new Object[]{id});
    }

    @Override
    public Token updateToken(String token, LocalDateTime issueAt, LocalDateTime ExpirAt, Long id) {
        String SQL = "update token" +
                "set refresh_token = ?," +
                "issue_at = ?," +
                "expired_at = ?," +
                "where [user] = ?";
        int result = jdbcTemplate.update(SQL, new Object[]{
                token,
                issueAt,
                ExpirAt,
                id
        });
        if (result==1)
            return Token.of(token);
        return null;
    }

    @Override
    public boolean removeToken(String token) {
        String SQL = "DELETE FROM token WHERE refresh_token = ?";
        int rowsAffected = jdbcTemplate.update(SQL, token);
        return rowsAffected > 0;
    }

    @Override
    public boolean isFindToken(String token) {
        String SQL = "SELECT COUNT(*) FROM token WHERE refresh_token = ?";
        int count = jdbcTemplate.queryForObject(
                SQL,
                new Object[]{token},
                Integer.class
        );

        if (count > 0){
            return true;
        }
        else {
            return false;
        }


    }
}

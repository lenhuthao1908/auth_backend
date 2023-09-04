package com.backend.auth.Repo.database;

import com.backend.auth.Repo.IPasswordRecoveryRepo;
import com.backend.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PasswordRecoveryRepo implements IPasswordRecoveryRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public void addPasswordRecovery(String token, Long id) {
        String SQL = "INSERT INTO password_recovery VALUES (?, ?)";
        jdbcTemplate.update(SQL, token, id);
    }

    @Override
    public User getTokenByUserId(Long id) {
        String SQL = "select * from password_recovery where [user] = ?";
        return jdbcTemplate.queryForObject(SQL, new Object[]{id}, new BeanPropertyRowMapper<>(User.class));
    }


}

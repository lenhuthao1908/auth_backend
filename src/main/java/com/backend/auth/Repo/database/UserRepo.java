package com.backend.auth.Repo.database;

import com.backend.auth.Repo.IUserRepo;
import com.backend.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserRepo implements IUserRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user){
        String SQL = "insert into [User]\n" +
                "values(?, ?, ?, ?, ?)";
        int result = jdbcTemplate.update(SQL, new Object[]{
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        });
        if(result == 1){
            return user;
        }else{
            return null;
        }
//        return jdbcTemplate.queryForObject(SQL, new Object[]{
//                user.getFirstname(),
//                user.getLastname(),
//                user.getEmail(),
//                user.getPassword(),
//                "100"
//        }, new BeanPropertyRowMapper<>(User.class));s

    }

    @Override
    public User findByEmail(String email) {
        String SQL = "SELECT * FROM [user] WHERE email = ?";
        return jdbcTemplate.queryForObject(SQL, new Object[]{email}, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public Optional<User> findById(Long id) {
        String SQL = "SELECT * FROM [user] WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(SQL, new Object[]{id}, new BeanPropertyRowMapper<>(User.class)));

    }

    @Override
    public Optional<User> findByIdAndTokensRefreshTokenAndTokensExpiredAtGreaterThan(Long id, String refreshToken, LocalDateTime expiredAt) {
        String SQL = "SELECT u.id, u.firstname, u.lastname, u.email, u.[password]\n" +
                "FROM [user] u\n" +
                "INNER JOIN token t ON u.id = t.[user]\n" +
                "WHERE u.id = ?\n" +
                "  AND t.refresh_token = ?\n" +
                "  AND t.expired_at >= ?";
        User user = jdbcTemplate.queryForObject(SQL, new Object[]{id, refreshToken, expiredAt}, new BeanPropertyRowMapper<>(User.class));
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByPasswordRecoveriesToken(String token) {
        String SQL = "SELECT u.id, u.firstname, u.lastname, u.email, u.password, pr.token, pr.[user] \n" +
                "FROM [user] u \n" +
                "INNER JOIN password_recovery pr ON u.id = pr.[user]\n" +
                "WHERE pr.token = ?";
        User user = jdbcTemplate.queryForObject(SQL, new Object[]{token}, new BeanPropertyRowMapper<>(User.class));
        return Optional.ofNullable(user);
    }

    @Override
    public String getPasswordRecoveriesOfUser(String token) {
        String SQL = "SELECT pr.token \n" +
                "FROM [user] u \n" +
                "INNER JOIN password_recovery pr ON u.id = pr.[user]\n" +
                "WHERE pr.token = ?";
        return String.valueOf(jdbcTemplate.queryForObject(SQL, new Object[]{token}, new BeanPropertyRowMapper<>(User.class)));

    }

    @Override
    public User update(String password, Long id) {
        String SQL = "update [user]\n" +
                "set password = ?,\n" +
                "tfa_secret = ?\n" +
                "where id = ?";
        jdbcTemplate.update(SQL, new Object[]{
                password,
                "100",
                id
        });
        return null;
    }

    @Override
    public void delete(Long id) {
        String SQL = "DELETE pr\n" +
                "FROM [password_recovery] pr\n" +
                "INNER JOIN [user] u ON u.id = pr.[user]\n" +
                "WHERE pr.token = ?;";
        jdbcTemplate.update(SQL, new Object[]{id});
    }

    @Override
    public boolean isTokenpr(String token) {
        String SQL = "select count(*) from [user] u\n" +
                "inner join password_recovery pr on u.id = pr.[user]\n" +
                "where pr.token = ?";
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

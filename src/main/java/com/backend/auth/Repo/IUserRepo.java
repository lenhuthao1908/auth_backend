package com.backend.auth.Repo;

import com.backend.auth.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IUserRepo {
    public User save(User user);

    public User findByEmail(String email);

    public Optional<User> findById(Long id);

    public Optional<User> findByIdAndTokensRefreshTokenAndTokensExpiredAtGreaterThan(Long id, String refreshToken, LocalDateTime expiredAt);

    public Optional<User> findByPasswordRecoveriesToken(String token);

    public String getPasswordRecoveriesOfUser(String token);

    public User update(String password, Long id);

    public void delete(Long id);

    public boolean isTokenpr(String token);
}

package com.backend.auth.service;

import com.backend.auth.entity.Login;
import com.backend.auth.entity.User;

public interface IAuthService {
    public User register(String firstName, String lastName, String email, String password, String passwordConfirm, String role);

    public Login login(String email, String password);

    public Login refreshAccess(String refreshToken);

    public Boolean logout(String refreshToken);

    public void forgot(String email, String originUrl);

    public boolean reset(String password, String passwordConfirm, String token);
}

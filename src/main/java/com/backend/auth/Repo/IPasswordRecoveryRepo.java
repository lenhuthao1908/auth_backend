package com.backend.auth.Repo;

import com.backend.auth.entity.User;

public interface IPasswordRecoveryRepo {
    public void addPasswordRecovery(String token, Long id);
    public User getTokenByUserId(Long id);
}

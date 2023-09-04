package com.backend.auth.service;

public interface IMailService {
    public void sendForgotMessage(String email, String token, String baseUrl);
}

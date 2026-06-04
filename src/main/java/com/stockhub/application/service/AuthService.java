package com.stockhub.application.service;

import com.stockhub.application.dto.LoginRequest;
import com.stockhub.application.dto.TokenPairResponse;

public interface AuthService {

    TokenPairResponse login(LoginRequest request);

    TokenPairResponse refresh(String refreshToken);

    void logout(String refreshToken);
}

package com.bank.service;

import com.bank.dao.request.SignUpRequest;
import com.bank.dao.request.SigninRequest;
import com.bank.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SigninRequest request);

    void blacklistToken(String token);
}

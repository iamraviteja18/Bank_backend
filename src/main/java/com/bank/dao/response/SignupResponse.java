package com.bank.dao.response;

import com.bank.entities.User;

public class SignupResponse {
    private String token;
    private User user; // Assume this is a DTO if you want to hide certain user properties

    public SignupResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    // Getters

    // Static method for building the response
    public static SignupResponse of(String token, User user) {
        return new SignupResponse(token, user);
    }
}

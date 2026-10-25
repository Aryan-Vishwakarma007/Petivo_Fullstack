package com.petivo.api.model;

import com.petivo.api.dto.UserDto;

public class LoginResponse {
    private boolean success;
    private String token;
    private String role;
    private String message;
    private UserDto user;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String role, String message) {
        this.success = success;
        this.role = role;
        this.message = message;
    }

    public LoginResponse(boolean success, String token, String role, String message, UserDto user) {
        this.success = success;
        this.token = token;
        this.role = role;
        this.message = message;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}

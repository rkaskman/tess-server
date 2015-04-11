package com.ttu.roman.controller.response;

public class UserResponse {
    public String userId;
    public String name;

    public UserResponse(String userId, String name) {
        this.name = name;
        this.userId = userId;
    }
}

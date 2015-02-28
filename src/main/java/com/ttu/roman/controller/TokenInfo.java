package com.ttu.roman.controller;

import java.util.Date;

public class TokenInfo {
    String userId;
    String audience;
    Date expiryTime;

    public Date getExpiresIn() {
        return expiryTime;
    }

    public void setExpiresIn(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

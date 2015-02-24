package com.ttu.roman.util;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {
    @Value("${businessRegisterURL}")
    private String businessRegisterURL;
    @Value("${googleAPIKey}")
    private String googleAPIKey;

    public String getBusinessRegisterURL() {
        return businessRegisterURL;
    }

    public String getGoogleAPIKey() {
        return googleAPIKey;
    }
}

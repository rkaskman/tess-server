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
    @Value("${googleClientId}")
    private String googleClientId;
    @Value("${tempFileDir}")
    private String tempFileDir;

    public String getBusinessRegisterURL() {
        return businessRegisterURL;
    }

    public String getGoogleAPIKey() {
        return googleAPIKey;
    }

    public String getGoogleClientId() {
        return googleClientId;
    }

    public String getTempFileDir() {
        return tempFileDir;
    }
}

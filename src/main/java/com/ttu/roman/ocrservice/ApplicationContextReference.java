package com.ttu.roman.ocrservice;

import org.springframework.context.ApplicationContext;

public class ApplicationContextReference {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextReference.applicationContext = applicationContext;
    }
}

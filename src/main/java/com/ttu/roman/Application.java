package com.ttu.roman;

import com.ttu.roman.ocrservice.ImageProcessingExecutor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties
@ImportResource("classpath:spring-config.xml")
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        ApplicationContextReference.setApplicationContext(applicationContext);

        ImageProcessingExecutor imageProcessingExecutor = applicationContext.getBeanFactory().getBean(ImageProcessingExecutor.class);
        imageProcessingExecutor.start();
    }
}

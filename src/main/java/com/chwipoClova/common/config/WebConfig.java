package com.chwipoClova.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


public class WebConfig implements WebMvcConfigurer {
    @Value("${cors.origins}")
    private String[] origins;

    public static final String ALLOWED_METHOD_NAMES = "GET,POST,PATCH,PUT,DELETE,OPTIONS";
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origins) // 허용할 출처
                .allowedMethods(ALLOWED_METHOD_NAMES.split(","))
                .allowCredentials(true); // 허용할 HTTP method
    }
}

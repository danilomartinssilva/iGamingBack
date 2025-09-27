package com.lotus.igaming.igaming.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){

        String[] ALLOWED_ORIGINS = new String[]{
                "http://localhost:4200",
                "http://0.0.0.0:4200",
                "http://127.0.0.1:4200"
        };

        registry.addMapping("/**")
                .allowedOrigins(ALLOWED_ORIGINS)
                .allowedHeaders("*")
                .allowedMethods("GET","POST","DELETE","PATCH","PUT","OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

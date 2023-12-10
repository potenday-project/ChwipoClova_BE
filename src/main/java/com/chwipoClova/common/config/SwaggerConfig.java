package com.chwipoClova.common.config;

import com.chwipoClova.common.utils.JwtUtil;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String key = JwtUtil.ACCESS_TOKEN;
        String refreshKey = JwtUtil.REFRESH_TOKEN;

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(key)
                        .addList(refreshKey)
                )
                .info(apiInfo())
                .components(new Components()
                        .addSecuritySchemes(key, new SecurityScheme()
                                .name(key)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .bearerFormat("JWT"))
                        .addSecuritySchemes(refreshKey, new SecurityScheme()
                                .name(refreshKey)
                                .type(SecurityScheme.Type.APIKEY)
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER))

                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Springdoc")
                .description("Springdoc을 사용한 Swagger UI")
                .version("1.0.0");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}

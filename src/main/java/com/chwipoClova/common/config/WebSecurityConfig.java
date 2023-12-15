package com.chwipoClova.common.config;

import com.chwipoClova.common.filter.JwtAuthFilter;
import com.chwipoClova.common.service.JwtAuthenticationEntryPoint;
import com.chwipoClova.common.utils.JwtUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer ignoringCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/h2-console/**", "/swagger-ui/**", "/swagger-client/**", "/api-docs/**", "/css/**", "/js/**", "/json/**", "/image/**",
                "/favicon",
                "/v3/api-docs/**",
                "/swagger-ui.html"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final @NotNull  HttpSecurity http) throws Exception {
        http.httpBasic(HttpBasicConfigurer::disable)
                .cors(withDefaults())
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                //.requestMatchers("/**").permitAll().anyRequest().authenticated()
                                .requestMatchers("/interview/**", "/resume/**", "/"
                                ,"/user/getKakaoUrl","/user/kakaoLogin","/user/kakaoCallback","/user/logout"
                                ).permitAll().anyRequest().authenticated()


                                //.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()



                )
                .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception)-> exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
        ;
        return http.build();
    }
}

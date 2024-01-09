package com.chwipoClova.common.config;

import com.chwipoClova.common.filter.JwtAuthFilter;
import com.chwipoClova.common.service.JwtAuthenticationEntryPoint;
import com.chwipoClova.common.utils.JwtUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;

    @Value("${web.ignoring.url}")
    private String[] ignoringUrl;

    @Value("${web.authorize.url}")
    private String[] authorizeUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer ignoringCustomizer() {
        return (web) -> web.ignoring().requestMatchers(ignoringUrl);
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
                                .requestMatchers(authorizeUrl).permitAll().anyRequest().authenticated()
                                //.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                )
                .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception)-> exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
        ;
        return http.build();
    }
}

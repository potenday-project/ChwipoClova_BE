package com.chwipoClova.common.service;

import com.chwipoClova.common.exception.ExceptionCode;
import com.chwipoClova.common.response.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        setResponse(response, ExceptionCode.SECURITY.getMessage());
    }

    private void setResponse(HttpServletResponse response, String message) throws IOException {
        log.error("[exceptionHandle] AuthenticationEntryPoint = {}", message);
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(new CommonResponse<String>(ExceptionCode.SECURITY.getMessage(),null, ExceptionCode.SECURITY.getCode()) ));
    }
}
package com.chwipoClova.common.response;

import com.chwipoClova.common.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (selectedContentType.isCompatibleWith(MediaType.APPLICATION_JSON) && !StringUtils.startsWith(request.getURI().getPath(), "/api-docs/")) {
            if (body instanceof CommonException) {
                return ResponseUtil.response(((CommonException) body).getErrorCode(), null, ((CommonException) body).getMessage());
            } else if (body instanceof CommonResponse) {
                return ResponseUtil.response(((CommonResponse) body).getCode(), ((CommonResponse) body).getData(), ((CommonResponse) body).getMessage());
            } else {
                return ResponseUtil.response(String.valueOf(HttpStatus.OK.value()), body, HttpStatus.OK.getReasonPhrase());
            }
        } else {
            return body;
        }
    }
}

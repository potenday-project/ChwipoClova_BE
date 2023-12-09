package com.chwipoClova.common.response;

public class ResponseUtil {
    public static <T> CommonResponse<T> response(String code, T response, String message) {
        return new CommonResponse<>(code, response, message);
    }
}

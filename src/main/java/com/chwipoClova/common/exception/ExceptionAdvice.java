package com.chwipoClova.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    //    @ExceptionHandler(value = {NoUserExistException.class, WrongPasswordException.class})
    @ExceptionHandler(CommonException.class)
    public CommonException handleCommonException(CommonException e) {
        log.error("CommonException({}) - {}", e.getClass().getSimpleName(), e.getMessage());
        return e;
    }

    @ExceptionHandler(Exception.class)
    public CommonException handleException(Exception e) {
        log.error("Exception({}) - {}", e.getClass().getSimpleName(), e.getMessage());
        return new CommonException(ExceptionCode.SERVER_ERROR.getMessage(), ExceptionCode.SERVER_ERROR.getCode());
    }
}
